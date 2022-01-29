package com.jacobtread.alto.booster.processor.p47.status

import com.jacobtread.alto.booster.packets.p47.status.SServerInfoPacket
import com.jacobtread.alto.booster.processor.PacketProcessor
import com.jacobtread.alto.booster.processor.p47.PingPongProcessor

interface ClientStatusProcessor : PacketProcessor, PingPongProcessor {
    fun onServerInfo(packet: SServerInfoPacket)
}