package com.jacobtread.alto.booster.packets.p47.status

import com.google.gson.GsonBuilder
import com.jacobtread.alto.booster.*
import com.jacobtread.alto.booster.data.ServerStatusResponse
import com.jacobtread.alto.booster.processor.p47.status.ClientStatusProcessor
import com.jacobtread.alto.chat.ChatStyle
import com.jacobtread.alto.chat.Text
import com.jacobtread.alto.chat.TextSerializer
import com.jacobtread.alto.utils.json.EnumTypeAdapterFactory
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