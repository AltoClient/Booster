package com.jacobtread.alto.booster.packets.p47.status

import com.jacobtread.alto.booster.Packet
import com.jacobtread.alto.booster.PacketSerializer
import com.jacobtread.alto.booster.ProtocolState
import com.jacobtread.alto.booster.processor.p47.status.ServerStatusProcessor
import io.netty.buffer.ByteBuf

class CServerQueryPacket : Packet<ServerStatusProcessor> {
    override val id: Int = 0x00
    override val state: ProtocolState = ProtocolState.STATUS
    override fun process(processor: ServerStatusProcessor) = processor.onServerQuery()
    class Serializer : PacketSerializer<CServerQueryPacket> {
        override fun read(buf: ByteBuf) = CServerQueryPacket()
        override fun write(buf: ByteBuf, packet: CServerQueryPacket) {}
    }
}