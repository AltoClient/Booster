package me.jacobtread.mck.booster.compress

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import me.jacobtread.mck.booster.writeVarInt
import java.util.zip.Deflater

class BoostCompressor(var threshold: Int) : MessageToByteEncoder<ByteBuf>() {
    private val buffer = ByteArray(8192)
    private val deflater: Deflater = Deflater()

    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext, input: ByteBuf, out: ByteBuf) {
        val size = input.readableBytes()
        if (size < threshold) {
            out.writeVarInt(0)
            out.writeBytes(input)
        } else {
            val data = ByteArray(size)
            input.readBytes(data)
            out.writeVarInt(data.size)
            deflater.setInput(data, 0, size)
            deflater.finish()
            while (!deflater.finished()) {
                val j = deflater.deflate(buffer)
                out.writeBytes(buffer, 0, j)
            }
            deflater.reset()
        }
    }
}