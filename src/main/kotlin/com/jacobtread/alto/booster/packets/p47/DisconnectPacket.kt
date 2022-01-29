package com.jacobtread.alto.booster.packets.p47

import io.netty.buffer.ByteBuf
import com.jacobtread.alto.booster.Packet
import com.jacobtread.alto.booster.processor.PacketProcessor
import com.jacobtread.alto.booster.PacketSerializer
import com.jacobtread.alto.booster.ProtocolState
import com.jacobtread.alto.chat.Text
import com.jacobtread.alto.booster.readTextSafe
import com.jacobtread.alto.booster.writeText

data class DisconnectPacket(val reason: Text, override val id: Int) : Packet<PacketProcessor> {
    override fun process(processor: PacketProcessor) = processor.disconnect(reason)
    override val state: ProtocolState = if (id == LOGIN_ID) ProtocolState.LOGIN else ProtocolState.PLAY

    companion object {
        const val LOGIN_ID = 0x00
        const val PLAY_ID = 0x40
    }

    class Serializer(val id: Int) : PacketSerializer<DisconnectPacket> {
        override fun read(buf: ByteBuf) = DisconnectPacket(buf.readTextSafe(), id)
        override fun write(buf: ByteBuf, packet: DisconnectPacket) {
            buf.writeText(packet.reason)
        }
    }
}
