package com.jacobtread.mck.booster.processor.p47

import com.jacobtread.mck.booster.packets.p47.DisconnectPacket
import com.jacobtread.mck.booster.packets.p47.play.AbilitiesPacket
import com.jacobtread.mck.booster.packets.p47.play.KeepAlivePacket
import com.jacobtread.mck.booster.packets.p47.status.PingPongPacket
import com.jacobtread.mck.booster.processor.PacketProcessor

interface KeepAliveProcessor : PacketProcessor {
    fun onKeepAlive(packet: KeepAlivePacket)
}

interface AbilitiesProcessor : PacketProcessor {
    fun onAbilities(packet: AbilitiesPacket)
}

interface DisconnectProcessor : PacketProcessor {
    fun onDisconnect(packet: DisconnectPacket)
}

interface PingPongProcessor : PacketProcessor {
    fun onPingPong(packet: PingPongPacket)
}