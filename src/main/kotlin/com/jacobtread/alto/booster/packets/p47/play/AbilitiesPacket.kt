package com.jacobtread.alto.booster.packets.p47.play

import com.jacobtread.alto.booster.Packet
import com.jacobtread.alto.booster.PacketSerializer
import com.jacobtread.alto.booster.ProtocolState
import io.netty.buffer.ByteBuf
import com.jacobtread.alto.booster.processor.p47.AbilitiesProcessor

data class AbilitiesPacket(
    val invulnerable: Boolean,
    val flying: Boolean,
    val allowFlying: Boolean,
    val creativeMode: Boolean,
    val flySpeed: Float,
    val walkSpeed: Float,
    override val id: Int
) : Packet<AbilitiesProcessor> {
    override val state: ProtocolState = ProtocolState.PLAY
    override fun process(processor: AbilitiesProcessor) = processor.onAbilities(this)

    companion object {
        const val CLIENT_ID = 0x13
        const val SERVER_ID = 0x39
    }

    class Serializer(val id: Int) : PacketSerializer<AbilitiesPacket> {
        override fun read(buf: ByteBuf): AbilitiesPacket {
            val dataByte = buf.readByte().toInt()
            return AbilitiesPacket(
                (dataByte and 1) > 0,
                (dataByte and 2) > 0,
                (dataByte and 4) > 0,
                (dataByte and 8) > 0,
                buf.readFloat(),
                buf.readFloat(),
                id
            )
        }

        override fun write(buf: ByteBuf, packet: AbilitiesPacket) {
            var dataByte = 0
            if (packet.invulnerable) dataByte = dataByte or 1
            if (packet.flying) dataByte = dataByte or 2
            if (packet.allowFlying) dataByte = dataByte or 4
            if (packet.creativeMode) dataByte = dataByte or 8
            buf.writeByte(dataByte)
            buf.writeFloat(packet.flySpeed)
            buf.writeFloat(packet.walkSpeed)
        }
    }
}