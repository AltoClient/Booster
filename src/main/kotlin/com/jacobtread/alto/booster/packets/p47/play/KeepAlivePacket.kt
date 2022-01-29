package com.jacobtread.alto.booster.packets.p47.play

import com.jacobtread.alto.booster.*
import io.netty.buffer.ByteBuf
import com.jacobtread.alto.booster.processor.p47.KeepAliveProcessor

data class KeepAlivePacket(val nonce: Int) : Packet<KeepAliveProcessor> {
    override val state: ProtocolState = ProtocolState.PLAY
    override val id: Int = 0x00
    override fun process(processor: KeepAliveProcessor) = processor.onKeepAlive(this)
    class Serializer : PacketSerializer<KeepAlivePacket> {
        override fun read(buf: ByteBuf) = KeepAlivePacket(buf.readVarInt())
        override fun write(buf: ByteBuf, packet: KeepAlivePacket) {
            buf.writeVarInt(packet.nonce)
        }
    }
}