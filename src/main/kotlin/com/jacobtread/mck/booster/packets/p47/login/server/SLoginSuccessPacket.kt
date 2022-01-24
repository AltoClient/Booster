package com.jacobtread.mck.booster.packets.p47.login.server

import com.jacobtread.mck.authlib.GameProfile
import com.jacobtread.mck.booster.*
import com.jacobtread.mck.booster.processor.p47.login.ClientLoginProcessor
import io.netty.buffer.ByteBuf
import java.util.*

data class SLoginSuccessPacket(val profile: GameProfile) : Packet<ClientLoginProcessor> {
    override val id: Int = 0x02
    override fun process(processor: ClientLoginProcessor) = processor.onLoginSuccess(this)
    override val state: ProtocolState = ProtocolState.LOGIN

    class Serializer : PacketSerializer<SLoginSuccessPacket> {
        override fun read(buf: ByteBuf) = SLoginSuccessPacket(
            GameProfile(
                UUID.fromString(buf.readString(36)),
                buf.readString(16)
            )
        )

        override fun write(buf: ByteBuf, packet: SLoginSuccessPacket) {
            buf.writeString(packet.profile.id?.toString() ?: "")
            buf.writeString(packet.profile.name ?: "")
        }
    }
}