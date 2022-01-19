package com.jacobtread.mck.booster.processor.p47

import com.jacobtread.mck.booster.packets.p47.CHandshakePacket
import com.jacobtread.mck.booster.processor.PacketProcessor

interface HandshakeProcessor : PacketProcessor {
    fun onHandshake(packet: CHandshakePacket)
}