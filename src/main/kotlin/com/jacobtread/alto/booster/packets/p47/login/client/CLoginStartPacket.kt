package com.jacobtread.alto.booster.packets.p47.login.client

import com.jacobtread.alto.authlib.GameProfile
import com.jacobtread.alto.booster.*
import com.jacobtread.alto.booster.processor.p47.login.ServerLoginProcessor
import io.netty.buffer.ByteBuf

data class CLoginStartPacket(val gameProfile: GameProfile) : Packet<ServerLoginProcessor> {
    override val id: Int = 0x00
    override fun process(processor: ServerLoginProcessor) = processor.onLoginStart(this)
    override val state: ProtocolState = ProtocolState.LOGIN

    class Serializer : PacketSerializer<CLoginStartPacket> {
        override fun read(buf: ByteBuf) = CLoginStartPacket(GameProfile(null, buf.readString(16)))
        override fun write(buf: ByteBuf, packet: CLoginStartPacket) {
            buf.writeString(packet.gameProfile.name!!)
        }
    }
}