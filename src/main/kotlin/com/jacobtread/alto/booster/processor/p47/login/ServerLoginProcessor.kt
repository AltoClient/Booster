package com.jacobtread.alto.booster.processor.p47.login

import com.jacobtread.alto.booster.packets.p47.login.client.CEncryptionResponsePacket
import com.jacobtread.alto.booster.packets.p47.login.client.CLoginStartPacket
import com.jacobtread.alto.booster.processor.PacketProcessor

interface ServerLoginProcessor : PacketProcessor {

    fun onLoginStart(packet: CLoginStartPacket)
    fun onEncryptionResponse(packet: CEncryptionResponsePacket)

}