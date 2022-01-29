package com.jacobtread.alto.booster.packets.p47.status

import io.netty.buffer.ByteBuf
import com.jacobtread.alto.booster.Packet
import com.jacobtread.alto.booster.PacketSerializer
import com.jacobtread.alto.booster.ProtocolState
import com.jacobtread.alto.booster.processor.p47.PingPongProcessor

data class PingPongPacket(val time: Long) : Packet<PingPongProcessor> {
    override val id: Int = 0x01
    override val state: ProtocolState = ProtocolState.STATUS
    override fun process(processor: PingPongProcessor) = processor.onPingPong(this)
    class Serializer() : PacketSerializer<PingPongPacket> {
        override fun read(buf: ByteBuf) = PingPongPacket(buf.readLong())
        override fun write(buf: ByteBuf, packet: PingPongPacket) {
            buf.writeLong(packet.time)
        }
    }
}