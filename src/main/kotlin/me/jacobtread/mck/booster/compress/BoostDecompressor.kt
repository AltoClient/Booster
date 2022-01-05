package me.jacobtread.mck.booster.compress

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.DecoderException
import me.jacobtread.mck.booster.readVarInt
import java.util.zip.Inflater

class BoostDecompressor(var threshold: Int) : ByteToMessageDecoder() {
    private val inflater: Inflater = Inflater()

    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        if (input.readableBytes() != 0) {
            val size = input.readVarInt()
            if (size == 0) {
                out.add(input.readBytes(input.readableBytes()))
            } else {
                if (size < threshold) throw DecoderException("Badly compressed packet - size of $size is below server threshold of $threshold")
                if (size > 2097152) throw DecoderException("Badly compressed packet - size of $size is larger than protocol maximum of 2097152")
                val data = ByteArray(input.readableBytes())
                input.readBytes(data)
                inflater.setInput(data)
                val deflatedData = ByteArray(size)
                inflater.inflate(deflatedData)
                out.add(Unpooled.wrappedBuffer(deflatedData))
                inflater.reset()
            }
        }
    }
}