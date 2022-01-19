package com.jacobtread.mck.booster.packets.p47.status

import com.google.gson.GsonBuilder
import com.jacobtread.mck.booster.*
import com.jacobtread.mck.booster.data.ServerStatusResponse
import com.jacobtread.mck.booster.processor.p47.status.ClientStatusProcessor
import com.jacobtread.mck.chat.ChatStyle
import com.jacobtread.mck.chat.Text
import com.jacobtread.mck.chat.TextSerializer
import com.jacobtread.mck.utils.json.EnumTypeAdapterFactory
import io.netty.buffer.ByteBuf

data class SServerInfoPacket(val response: ServerStatusResponse) : Packet<ClientStatusProcessor> {
    override val id: Int = 0x00
    override val state: ProtocolState = ProtocolState.STATUS
    override fun process(processor: ClientStatusProcessor) = processor.onServerInfo(this)
    class Serializer : PacketSerializer<SServerInfoPacket> {
        private val gson = GsonBuilder().registerTypeAdapter(
            ServerStatusResponse.ProtocolVersionIdentifier::class.java,
            ServerStatusResponse.ProtocolVersionIdentifier.Serializer()
        ).registerTypeAdapter(
            ServerStatusResponse.PlayerCountData::class.java,
            ServerStatusResponse.PlayerCountData.Serializer()
        ).registerTypeAdapter(
            ServerStatusResponse::class.java,
            ServerStatusResponse.Serializer()
        ).registerTypeAdapter(
            Text::class.java,
            TextSerializer()
        ).registerTypeAdapter(
            ChatStyle::class.java,
            ChatStyle.Serializer()
        ).registerTypeAdapterFactory(EnumTypeAdapterFactory()).create()

        override fun read(buf: ByteBuf) =
            SServerInfoPacket(gson.fromJson(buf.readString(32767), ServerStatusResponse::class.java))

        override fun write(buf: ByteBuf, packet: SServerInfoPacket) {
            buf.writeString(gson.toJson(packet.response))
        }
    }
}