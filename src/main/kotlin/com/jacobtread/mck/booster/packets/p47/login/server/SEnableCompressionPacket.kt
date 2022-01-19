package com.jacobtread.mck.booster.packets.p47.login.server

import com.jacobtread.mck.booster.*
import io.netty.buffer.ByteBuf
import com.jacobtread.mck.booster.processor.p47.login.ClientLoginProcessor

data class SEnableCompressionPacket(val threshold: Int) : Packet<ClientLoginProcessor> {
    override val id: Int = 0x03
    override fun process(processor: ClientLoginProcessor) = processor.onEnableCompression(this)
    override val state: ProtocolState = ProtocolState.LOGIN
    class Serializer : PacketSerializer<SEnableCompressionPacket> {
        override fun read(buf: ByteBuf) = SEnableCompressionPacket(buf.readVarInt())
        override fun write(buf: ByteBuf, packet: SEnableCompressionPacket) {
            buf.writeVarInt(packet.threshold)
        }
    }
}