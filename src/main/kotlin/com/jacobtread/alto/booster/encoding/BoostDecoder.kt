package com.jacobtread.alto.booster.encoding

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.DecoderException
import com.jacobtread.alto.booster.Constants
import com.jacobtread.alto.booster.PacketMapper
import com.jacobtread.alto.booster.readVarInt

class BoostDecoder(val direction: Int, val packetMapper: PacketMapper) : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        if (input.readableBytes() == 0) return
        val id = input.readVarInt()
        val state = ctx.channel().attr(Constants.PROTOCOL_STATE_KEY).get()
        val serializer = packetMapper.getSerializer(state.id, direction, id)
            ?: throw DecoderException("Unknown packet. No packets mapped to id $id")
        val packet = serializer.read(input)
        val remaining = input.readableBytes()
        if (remaining > 0)
            throw DecoderException("Packet with id $id on state $state didn't read all data left ${remaining}bytes")
        out.add(packet)
    }
}