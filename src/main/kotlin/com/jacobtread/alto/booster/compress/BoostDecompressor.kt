package com.jacobtread.alto.booster.compress

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.DecoderException
import com.jacobtread.alto.booster.readVarInt
import java.util.zip.Inflater

class BoostDecompressor(var threshold: Int) : ByteToMessageDecoder() {
    private val inflater: Inflater = Inflater()

    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        if (input.readableBytes() != 0) {
            val i = input.readVarInt()
            if (i == 0) {
                out.add(input.readBytes(input.readableBytes()))
            } else {
                if (i < threshold) throw DecoderException("Badly compressed packet - size of $i is below server threshold of $threshold")
                if (i > 2097152) throw DecoderException("Badly compressed packet - size of $i is larger than protocol maximum of 2097152")
                val data = ByteArray(input.readableBytes())
                input.readBytes(data)
                inflater.setInput(data)
                val deflatedData = ByteArray(i)
                inflater.inflate(deflatedData)
                out.add(Unpooled.wrappedBuffer(deflatedData))
                inflater.reset()
            }
        }
    }
}