package com.jacobtread.mck.booster.packets.p47.status

import io.netty.buffer.ByteBuf
import com.jacobtread.mck.booster.Packet
import com.jacobtread.mck.booster.PacketSerializer
import com.jacobtread.mck.booster.ProtocolState
import com.jacobtread.mck.booster.processor.p47.PingPongProcessor

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