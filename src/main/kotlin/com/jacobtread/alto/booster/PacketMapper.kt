package com.jacobtread.alto.booster

interface PacketMapper {
    fun getSerializer(state: Int, direction: Int, id: Int): PacketSerializer<*>?
}