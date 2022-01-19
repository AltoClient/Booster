package com.jacobtread.mck.booster

interface PacketMapper {
    fun getSerializer(state: Int, direction: Int, id: Int): PacketSerializer<*>?
}