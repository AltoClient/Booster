package com.jacobtread.mck.booster

import com.jacobtread.mck.booster.processor.PacketProcessor
import io.netty.buffer.ByteBuf

interface Packet<P : PacketProcessor> {
    val id: Int
    val state: ProtocolState

    fun process(processor: P)
}

interface PacketSerializer<V : Packet<*>> {
    fun read(buf: ByteBuf): V
    fun write(buf: ByteBuf, packet: V)
}

class MalformedPacketException(cause: String) : RuntimeException(cause)