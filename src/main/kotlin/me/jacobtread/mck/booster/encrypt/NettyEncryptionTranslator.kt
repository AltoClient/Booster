package me.jacobtread.mck.booster.encrypt

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import javax.crypto.Cipher
import javax.crypto.ShortBufferException

class NettyEncryptionTranslator(private val cipher: Cipher) {
    private var tmp = ByteArray(0)
    private var tmp1 = ByteArray(0)

    private fun read(buf: ByteBuf): ByteArray {
        val i = buf.readableBytes()
        if (tmp.size < i) {
            tmp = ByteArray(i)
        }
        buf.readBytes(tmp, 0, i)
        return tmp
    }

    @Throws(ShortBufferException::class)
    fun decipher(ctx: ChannelHandlerContext, buffer: ByteBuf): ByteBuf {
        val readable = buffer.readableBytes()
        val data = read(buffer)
        val heaped = ctx.alloc().heapBuffer(cipher.getOutputSize(readable))
        heaped.writerIndex(cipher.update(data, 0, readable, heaped.array(), heaped.arrayOffset()))
        return heaped
    }

    @Throws(ShortBufferException::class)
    fun cipher(input: ByteBuf, out: ByteBuf) {
        val readable = input.readableBytes()
        val data = read(input)
        val newSize = cipher.getOutputSize(readable)
        if (tmp1.size < newSize) {
            tmp1 = ByteArray(newSize)
        }
        out.writeBytes(tmp1, 0, cipher.update(data, 0, readable, tmp1))
    }
}