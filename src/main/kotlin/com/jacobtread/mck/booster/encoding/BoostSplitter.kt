package com.jacobtread.mck.booster.encoding

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.CorruptedFrameException
import com.jacobtread.mck.booster.readVarInt

class BoostSplitter : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        input.markReaderIndex()
        val bytes = ByteArray(3)
        for (i in bytes.indices) {
            if (!input.isReadable) {
                input.resetReaderIndex()
                return
            }
            bytes[i] = input.readByte()
            if (bytes[i] >= 0) {
                val data = Unpooled.wrappedBuffer(bytes)
                try {
                    val size = data.readVarInt()
                    if (input.readableBytes() >= size) {
                        out.add(input.readBytes(size))
                        return
                    }
                    input.resetReaderIndex()
                } finally {
                    data.release()
                }
                return
            }
        }
        throw CorruptedFrameException("length wider than 21-bit")
    }
}