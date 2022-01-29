package com.jacobtread.alto.booster.packets.p47.login.client

import com.jacobtread.alto.booster.*
import com.jacobtread.alto.booster.processor.p47.login.ServerLoginProcessor
import io.netty.buffer.ByteBuf

data class CEncryptionResponsePacket(val secretKeyBytes: ByteArray, val verifyTokenBytes: ByteArray) :
    Packet<ServerLoginProcessor> {
    override val id: Int = 0x1
    override val state: ProtocolState = ProtocolState.LOGIN
    override fun process(processor: ServerLoginProcessor) = processor.onEncryptionResponse(this)
    class Serializer : PacketSerializer<CEncryptionResponsePacket> {
        override fun read(buf: ByteBuf) = CEncryptionResponsePacket(buf.readByteArray(), buf.readByteArray())
        override fun write(buf: ByteBuf, packet: CEncryptionResponsePacket) {
            buf.writeByteArray(packet.secretKeyBytes)
            buf.writeByteArray(packet.verifyTokenBytes)
        }
    }
}