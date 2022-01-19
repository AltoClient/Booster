package com.jacobtread.mck.booster

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.EncoderException
import java.nio.charset.StandardCharsets
import java.util.*

const val ZERO_BYTE: Byte = 0
const val ZERO_SHORT: Short = 0
const val DEFAULT_MAX_LENGTH = 32767

fun ByteBuf.readTinyBoolean() = readByte() != ZERO_BYTE
fun ByteBuf.readTinyUnsignedBoolean() = readUnsignedByte() != ZERO_SHORT
fun ByteBuf.writeTinyBoolean(value: Boolean): ByteBuf? = writeByte(if (value) 1 else 0)
fun ByteBuf.readByteArray(): ByteArray {
    val data = ByteArray(readVarInt())
    readBytes(data)
    return data
}

fun ByteBuf.writeByteArray(byteArray: ByteArray) {
    writeVarInt(byteArray.size)
    writeBytes(byteArray)
}

fun ByteBuf.writeString(data: String) {
    val bytes = data.toByteArray(StandardCharsets.UTF_8)
    if (bytes.size > DEFAULT_MAX_LENGTH) throw EncoderException("String value too big (was ${bytes.size} encoded, max $DEFAULT_MAX_LENGTH)")
    writeByteArray(bytes)
}

fun ByteBuf.readString(maxLength: Int = DEFAULT_MAX_LENGTH): String {
    val length = readVarInt()
    if (length > maxLength * 4) throw DecoderException("The received string was longer than the expected length (${length / 4} > $maxLength")
    if (length < 0) throw DecoderException("The received string was empty")
    val bytes = ByteArray(length)
    readBytes(bytes)
    return String(bytes, StandardCharsets.UTF_8)
}

fun ByteBuf.readUUID() = UUID(readLong(), readLong())
fun ByteBuf.writeUUID(uuid: UUID) {
    writeLong(uuid.mostSignificantBits)
    writeLong(uuid.leastSignificantBits)
}

fun ByteBuf.readVarInt(): Int {
    var value = 0
    var bitOffset = 0
    var currentByte: Byte
    do {
        if (bitOffset == 35) throw RuntimeException("VarInt is too big")
        currentByte = readByte()
        value = value or ((currentByte.toInt() and 127) shl bitOffset)
        bitOffset += 7
    } while (currentByte.toInt() and 128 != 0)
    return value
}

fun ByteBuf.writeVarInt(value: Int) {
    var v = value
    while (true) {
        if (v and -128 == 0) {
            writeByte(v)
            return
        }
        writeByte((v and 127) or 128)
        v = v ushr 7
    }
}

fun ByteBuf.readVarLong(): Long {
    var value: Long = 0
    var bitOffset = 0
    var currentByte: Int
    do {
        if (bitOffset == 70) throw RuntimeException("VarLong is too big")
        currentByte = readByte().toInt()
        value = value or ((currentByte and 127).toLong() shl bitOffset)
        bitOffset += 7
    } while ((currentByte and 128) != 0)
    return value
}

fun ByteBuf.writeVarLong(value: Long) {
    var v = value
    while (true) {
        if (v and -128 == 0L) {
            writeByte(v.toInt())
            return
        }
        writeByte(((v and 127) or 128).toInt())
        v = v ushr 7
    }
}


fun getVarIntSize(input: Int): Int {
    var value = 1
    while (value < 5) {
        if ((input and (-1 shl value * 7)) == 0) {
            return value
        }
        value++
    }
    return 5
}