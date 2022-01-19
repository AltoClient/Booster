package com.jacobtread.mck.booster.processor.p47.status

import com.jacobtread.mck.booster.packets.p47.status.SServerInfoPacket
import com.jacobtread.mck.booster.processor.PacketProcessor
import com.jacobtread.mck.booster.processor.p47.PingPongProcessor

interface ClientStatusProcessor : PacketProcessor, PingPongProcessor {
    fun onServerInfo(packet: SServerInfoPacket)
}