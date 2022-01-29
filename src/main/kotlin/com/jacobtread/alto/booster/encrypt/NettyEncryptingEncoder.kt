package com.jacobtread.alto.booster.encrypt

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import javax.crypto.Cipher

class NettyEncryptingEncoder(cipher: Cipher) : MessageToByteEncoder<ByteBuf>() {
    private val encryptionCodec = NettyEncryptionTranslator(cipher)
    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        encryptionCodec.cipher(msg, out)
    }
}