package com.jacobtread.alto.booster

enum class ProtocolState(val id: Int) {
    HANDSHAKING(-1),
    PLAY(0),
    STATUS(1),
    LOGIN(2);

    companion object {

        fun getById(id: Int): ProtocolState = when (id) {
            -1 -> HANDSHAKING
            0 -> PLAY
            1 -> STATUS
            2 -> LOGIN
            else -> throw MalformedPacketException("Unexpected protocol state ID")
        }
    }
}