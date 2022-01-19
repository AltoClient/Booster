package me.jacobtread.mck.booster.encrypt

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import javax.crypto.Cipher

class NettyEncryptingDecoder(cipher: Cipher) : MessageToMessageDecoder<ByteBuf>() {
    private val decryptionCodec = me.jacobtread.mck.booster.encrypt.NettyEncryptionTranslator(cipher)
    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        out.add(decryptionCodec.decipher(ctx, msg))
    }
}