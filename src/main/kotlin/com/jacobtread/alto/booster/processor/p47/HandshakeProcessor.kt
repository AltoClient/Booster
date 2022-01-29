package com.jacobtread.alto.booster.processor.p47

import com.jacobtread.alto.booster.packets.p47.CHandshakePacket
import com.jacobtread.alto.booster.processor.PacketProcessor

interface HandshakeProcessor : PacketProcessor {
    fun onHandshake(packet: CHandshakePacket)
}