package com.jacobtread.alto.booster.packets.p47.login.server

import com.jacobtread.alto.booster.*
import com.jacobtread.alto.booster.processor.p47.login.ClientLoginProcessor
import com.jacobtread.alto.utils.Crypt
import io.netty.buffer.ByteBuf
import java.security.PublicKey

data class SEncryptionRequestPacket(val serverId: String, val publicKey: PublicKey, val verifyToken: ByteArray) :
    Packet<ClientLoginProcessor> {
    override val id: Int = 0x01
    override fun process(processor: ClientLoginProcessor) = processor.onRequestEncryption(this)
    override val state: ProtocolState = ProtocolState.LOGIN

    class Serializer : PacketSerializer<SEncryptionRequestPacket> {
        override fun read(buf: ByteBuf) = SEncryptionRequestPacket(
            buf.readString(20),
            Crypt.decodePublicKey(buf.readByteArray()),
            buf.readByteArray()
        )

        override fun write(buf: ByteBuf, packet: SEncryptionRequestPacket) {
            buf.writeString(packet.serverId)
            buf.writeByteArray(packet.publicKey.encoded)
            buf.writeByteArray(packet.verifyToken)
        }
    }
}