package com.jacobtread.mck.booster.processor.p47.login

import com.jacobtread.mck.booster.packets.p47.login.client.CEncryptionResponsePacket
import com.jacobtread.mck.booster.packets.p47.login.client.CLoginStartPacket
import com.jacobtread.mck.booster.processor.PacketProcessor

interface ServerLoginProcessor : PacketProcessor {

    fun onLoginStart(packet: CLoginStartPacket)
    fun onEncryptionResponse(packet: CEncryptionResponsePacket)

}