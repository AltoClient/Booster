package com.jacobtread.alto.booster.encoding

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.EncoderException
import io.netty.handler.codec.MessageToByteEncoder
import com.jacobtread.alto.booster.getVarIntSize
import com.jacobtread.alto.booster.writeVarInt

class BoostJoiner : MessageToByteEncoder<ByteBuf>() {
    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        val size = msg.readableBytes()
        val varIntSize = getVarIntSize(size)
        if (varIntSize > 3) throw EncoderException("Unable to fit $size into 3")
        out.ensureWritable(varIntSize + size)
        out.writeVarInt(size)
        out.writeBytes(msg, msg.readerIndex(), size)
    }
}