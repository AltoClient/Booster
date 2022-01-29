package com.jacobtread.alto.booster.encoding

import com.jacobtread.alto.booster.*
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.MessageToByteEncoder

class BoostEncoder(val direction: Int, val packetMapper: PacketMapper) : MessageToByteEncoder<Packet<*>>() {

    override fun encode(ctx: ChannelHandlerContext, msg: Packet<*>, out: ByteBuf) {
        val id = msg.id
        val state = ctx.channel().attr(Constants.PROTOCOL_STATE_KEY).get()
        val serializer: PacketSerializer<*> = packetMapper.getSerializer(state.id, direction, id)
            ?: throw DecoderException("Unknown packet. No packets mapped to id $id")
        out.writeVarInt(id)
        try {
            out.writePacket(serializer, msg)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun <V : Packet<*>> ByteBuf.writePacket(packetEncoder: PacketSerializer<V>, packet: Packet<*>) {
        @Suppress("UNCHECKED_CAST")
        return packetEncoder.write(this, packet as V)
    }

}