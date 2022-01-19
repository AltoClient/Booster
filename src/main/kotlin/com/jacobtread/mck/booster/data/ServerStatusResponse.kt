package com.jacobtread.mck.booster.data

import com.google.gson.*
import com.jacobtread.mck.chat.Text
import com.jacobtread.mck.utils.json.expectInt
import com.jacobtread.mck.utils.json.expectString
import com.mojang.authlib.GameProfile
import java.lang.reflect.Type
import java.util.*

class ServerStatusResponse {

    var serverMOTD: Text? = null
    var playerCount: PlayerCountData? = null
    var protocolVersion: ProtocolVersionIdentifier? = null
    var favicon: String? = null

    data class ProtocolVersionIdentifier(val name: String, val protocol: Int) {
        class Serializer : JsonDeserializer<ProtocolVersionIdentifier>, JsonSerializer<ProtocolVersionIdentifier> {
            override fun deserialize(
                json: JsonElement,
                typeOfT: Type,
                context: JsonDeserializationContext
            ): ProtocolVersionIdentifier {
                val versionObject = json.asJsonObject
                return ProtocolVersionIdentifier(
                    versionObject.expectString("name"),
                    versionObject.expectInt("protocol")
                )
            }

            override fun serialize(
                src: ProtocolVersionIdentifier,
                typeOfSrc: Type,
                context: JsonSerializationContext
            ): JsonElement {
                return JsonObject().apply {
                    addProperty("name", src.name)
                    addProperty("protocol", src.protocol)
                }
            }

        }
    }

    data class PlayerCountData(val maxPlayers: Int, val onlinePlayerCount: Int, val players: Array<GameProfile>) {
        class Serializer : JsonDeserializer<PlayerCountData>, JsonSerializer<PlayerCountData> {
            override fun deserialize(
                json: JsonElement,
                typeOfT: Type,
                context: JsonDeserializationContext
            ): PlayerCountData {
                val playersObject = json.asJsonObject
                val max = playersObject.expectInt("max")
                val online = playersObject.expectInt("online")
                val players = if (playersObject.has("sample") && playersObject["sample"] is JsonArray) {
                    val sampleArray = playersObject["sample"].asJsonArray
                    if (sampleArray.size() > 0) {
                        val players: Array<GameProfile> = Array(sampleArray.size()) {
                            val playerObject = sampleArray.get(it).asJsonObject
                            val id = playerObject.expectString("id")
                            val name = playerObject.expectString("name")
                            GameProfile(UUID.fromString(id), name)
                        }
                        players
                    } else emptyArray()
                } else emptyArray()
                return PlayerCountData(max, online, players)
            }

            override fun serialize(
                src: PlayerCountData,
                typeOfSrc: Type,
                context: JsonSerializationContext
            ): JsonElement {
                return JsonObject().apply {
                    addProperty("max", src.maxPlayers)
                    addProperty("online", src.onlinePlayerCount)
                    if (src.players.isNotEmpty()) {
                        add("sample", JsonArray().apply {
                            src.players.map { player ->
                                add(JsonObject().apply {
                                    val uuid = player.id
                                    val name = player.name
                                    addProperty("id", uuid?.toString() ?: "")
                                    addProperty("name", name)
                                })
                            }
                        })
                    }
                }
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PlayerCountData

            if (maxPlayers != other.maxPlayers) return false
            if (onlinePlayerCount != other.onlinePlayerCount) return false
            if (!players.contentEquals(other.players)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = maxPlayers
            result = 31 * result + onlinePlayerCount
            result = 31 * result + players.contentHashCode()
            return result
        }
    }

    class Serializer : JsonDeserializer<ServerStatusResponse>, JsonSerializer<ServerStatusResponse> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): ServerStatusResponse {
            val statusObject = json.asJsonObject
            val serverStatusResponse = ServerStatusResponse()
            if (statusObject.has("description")) {
                serverStatusResponse.serverMOTD =
                    context.deserialize(statusObject.get("description"), Text::class.java)
            }
            if (statusObject.has("players")) {
                serverStatusResponse.playerCount =
                    context.deserialize(statusObject.get("players"), PlayerCountData::class.java)
            }
            if (statusObject.has("version")) {
                serverStatusResponse.protocolVersion =
                    context.deserialize(statusObject.get("version"), ProtocolVersionIdentifier::class.java)
            }
            if (statusObject.has("favicon")) {
                serverStatusResponse.favicon = statusObject.expectString("favicon")
            }
            return serverStatusResponse
        }

        override fun serialize(
            src: ServerStatusResponse,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return JsonObject().apply {
                if (src.serverMOTD != null) {
                    add("description", context.serialize(src.serverMOTD, Text::class.java))
                }
                if (src.playerCount != null) {
                    add("players", context.serialize(src.playerCount, PlayerCountData::class.java))
                }
                if (src.protocolVersion != null) {
                    add("version", context.serialize(src.protocolVersion, ProtocolVersionIdentifier::class.java))
                }
                if (src.favicon != null) addProperty("favicon", src.favicon)
            }
        }
    }
}


