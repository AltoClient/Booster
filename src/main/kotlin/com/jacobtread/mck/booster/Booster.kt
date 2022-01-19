package com.jacobtread.mck.booster

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.jacobtread.mck.booster.compress.BoostCompressor
import com.jacobtread.mck.booster.compress.BoostDecompressor
import com.jacobtread.mck.booster.encoding.BoostDecoder
import com.jacobtread.mck.booster.encoding.BoostEncoder
import com.jacobtread.mck.booster.encoding.BoostJoiner
import com.jacobtread.mck.booster.encoding.BoostSplitter
import com.jacobtread.mck.booster.encrypt.NettyEncryptingDecoder
import com.jacobtread.mck.booster.encrypt.NettyEncryptingEncoder
import com.jacobtread.mck.booster.processor.PacketProcessor
import com.jacobtread.mck.chat.Text
import com.jacobtread.mck.chat.types.LiteralText
import com.jacobtread.mck.chat.types.TranslationText
import com.jacobtread.mck.utils.Crypt
import com.jacobtread.mck.utils.Tickable
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollSocketChannel
import io.netty.channel.local.LocalChannel
import io.netty.channel.local.LocalServerChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.TimeoutException
import java.net.InetAddress
import java.net.SocketAddress
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.crypto.SecretKey
import kotlin.concurrent.read
import kotlin.concurrent.write

class Booster : SimpleChannelInboundHandler<Packet<*>>() {

    companion object {
        const val SERVER_PACKETS = 1
        const val CLIENT_PACKETS = 0

        private val NIO_EVENT_LOOP: NioEventLoopGroup by lazy {
            NioEventLoopGroup(
                0, ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build()
            )
        }

        private val EPOLL_EVENT_LOOP: EpollEventLoopGroup by lazy {
            EpollEventLoopGroup(
                0, ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build()
            )
        }

        private val LOCAL_EVENT_LOOP: DefaultEventLoopGroup by lazy {
            DefaultEventLoopGroup(
                0, ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build()
            )
        }

        fun createAndConnect(
            address: InetAddress,
            port: Int,
            useNativeTransport: Boolean,
            packetMapper: PacketMapper
        ): Booster {
            val booster = Booster()
            val useNative = Epoll.isAvailable() && useNativeTransport
            val channelClass = if (useNative) EpollSocketChannel::class.java else NioSocketChannel::class.java
            val eventLoopLazy = if (useNative) EPOLL_EVENT_LOOP else NIO_EVENT_LOOP
            Bootstrap().group(eventLoopLazy).handler(object : ChannelInitializer<Channel>() {
                override fun initChannel(ch: Channel) {
                    try {
                        ch.config().setOption(ChannelOption.TCP_NODELAY, true)
                    } catch (_: ChannelException) {
                    }
                    ch.pipeline().addLast("timeout", ReadTimeoutHandler(30)).addLast("splitter", BoostSplitter())
                        .addLast("decoder", BoostDecoder(SERVER_PACKETS, packetMapper))
                        .addLast("prepender", BoostJoiner())
                        .addLast("encoder", BoostEncoder(CLIENT_PACKETS, packetMapper))
                        .addLast("packet_handler", booster)
                }
            }).channel(channelClass).connect(address, port).syncUninterruptibly()
            return booster
        }

        @JvmStatic
        fun createLocalClient(address: SocketAddress): Booster {
            val booster = Booster()
            Bootstrap().group(LOCAL_EVENT_LOOP).handler(object : ChannelInitializer<Channel>() {
                override fun initChannel(ch: Channel) {
                    ch.pipeline().addLast("packet_handler", booster)
                }
            }).channel(LocalChannel::class.java).connect(address).syncUninterruptibly()
            return booster
        }

    }

    data class PacketQueueItem(val packet: Packet<*>, val callback: Runnable?)

    lateinit var channel: Channel
    lateinit var socketAddress: SocketAddress
    lateinit var processor: PacketProcessor

    var isEncrypted = false
    var isDisconnected = false

    var protocolState: ProtocolState
        get() = channel.attr(Constants.PROTOCOL_STATE_KEY).get() ?: ProtocolState.HANDSHAKING
        set(value) {
            channel.attr(Constants.PROTOCOL_STATE_KEY).set(value)
            channel.config().isAutoRead = true
        }

    val isLocal get() = channel is LocalChannel || channel is LocalServerChannel
    val isChannelOpen get() = this::channel.isInitialized && channel.isOpen

    val outboundQueue = ConcurrentLinkedQueue<PacketQueueItem>()
    val lock = ReentrantReadWriteLock()

    val hasNoChannel get() = !this::channel.isInitialized

    private lateinit var terminationReason: Text


    override fun channelActive(ctx: ChannelHandlerContext) {
        super.channelActive(ctx)
        channel = ctx.channel()
        socketAddress = channel.remoteAddress()
        try {
            protocolState = ProtocolState.HANDSHAKING
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        closeChannel(TranslationText("disconnect.endOfStream"))
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Packet<*>) {
        if (!isChannelOpen) return
        processPacket(msg)
    }

    fun <P : PacketProcessor> processPacket(msg: Packet<P>) {
        try {
            @Suppress("UNCHECKED_CAST") msg.process(processor as P)
        } catch (_: ThreadQuickExitException) {
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        val message = if (cause is TimeoutException) TranslationText("disconnect.timeout")
        else TranslationText("disconnect.genericReason", "${cause.javaClass.simpleName}: ${cause.message}")
        closeChannel(message)
    }

    fun sendPacket(packet: Packet<*>, callback: Runnable? = null) {
        if (isChannelOpen) {
            flushOutboundQueue()
            dispatchPacket(packet, callback)
        } else {
            lock.write { outboundQueue.add(PacketQueueItem(packet, callback)) }
        }
    }

    fun flushOutboundQueue() {
        lock.read {
            while (outboundQueue.isNotEmpty()) {
                val item = outboundQueue.poll()
                dispatchPacket(item.packet, item.callback)
            }
        }
    }

    fun processReceivedPackets() {
        if (isChannelOpen) {
            flushOutboundQueue()
        }

        if (this::processor.isInitialized) {
            val processor = processor
            if (processor is Tickable) processor.tick()
        }
        channel.flush()
    }

    fun dispatchPacket(packet: Packet<*>, callback: Runnable?) {
        if (packet.state != protocolState) {
            channel.config().isAutoRead = false
        }
        with(channel.eventLoop()) {
            if (inEventLoop()) {
                if (packet.state != protocolState) {
                    protocolState = packet.state
                }
                val future = channel.writeAndFlush(packet)
                future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                if (callback != null) future.addListener { callback.run() }
            } else {
                execute {
                    if (packet.state != protocolState) {
                        protocolState = packet.state
                    }
                    val future = channel.writeAndFlush(packet)
                    future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                    if (callback != null) future.addListener { callback.run() }
                }
            }
        }
    }

    fun closeChannel(message: Text) {
        terminationReason = message
        if (isChannelOpen) {
            channel.close().awaitUninterruptibly()
        }
    }

    fun enableEncryption(key: SecretKey) {
        isEncrypted = true
        with(channel.pipeline()) {
            addBefore(
                "splitter", "decrypt", NettyEncryptingDecoder(Crypt.createNetCipher(2, key))
            )
            addBefore(
                "prepender", "encrypt", NettyEncryptingEncoder(Crypt.createNetCipher(1, key))
            )
        }
    }

    fun setCompressionThreshold(threshold: Int) {
        channel.pipeline().apply {
            if (threshold >= 0) {
                val decompress = get("decompress")
                if (decompress is BoostDecompressor) decompress.threshold = threshold
                else addBefore(
                    "decoder", "decompress", BoostDecompressor(threshold)
                )
                val compress = get("compress")
                if (compress is BoostCompressor) compress.threshold = threshold
                else addBefore(
                    "encoder", "compress", BoostCompressor(threshold)
                )
            } else {
                val decompress = get("decompress")
                if (decompress is BoostDecompressor) remove("decompress")
                val compress = get("compress")
                if (compress is BoostCompressor) remove("compress")
            }
        }
    }

    fun disableAutoRead() {
        channel.config().isAutoRead = false
    }

    fun handleDisconnect() {
        if (this::channel.isInitialized && !channel.isOpen) {
            if (!isDisconnected) {
                isDisconnected = true
                if (this::terminationReason.isInitialized) {
                    processor.disconnect(terminationReason)
                } else {
                    processor.disconnect(LiteralText("Disconnected"))
                }
            }
        }
    }
}