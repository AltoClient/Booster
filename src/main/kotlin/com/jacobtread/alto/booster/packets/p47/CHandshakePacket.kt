package com.jacobtread.alto.booster.packets.p47

import com.jacobtread.alto.booster.*
import io.netty.buffer.ByteBuf
import com.jacobtread.alto.booster.processor.p47.HandshakeProcessor

data class CHandshakePacket(
    val version: Int,
    val ip: String,
    val port: Int,
    val requestedState: ProtocolState
) : Packet<HandshakeProcessor> {
    override val id: Int = 0x00
    override val state: ProtocolState = ProtocolState.HANDSHAKING
    override fun process(processor: HandshakeProcessor) = processor.onHandshake(this)
    class Serializer : PacketSerializer<CHandshakePacket> {
        override fun read(buf: ByteBuf) = CHandshakePacket(
            buf.readVarInt(),
            buf.readString(255),
            buf.readUnsignedShort(),
            ProtocolState.getById(buf.readVarInt())
        )

        override fun write(buf: ByteBuf, packet: CHandshakePacket) {
            buf.writeVarInt(packet.version)
            buf.writeString(packet.ip)
            buf.writeShort(packet.port)
            buf.writeVarInt(packet.requestedState.id)
        }
    }
}