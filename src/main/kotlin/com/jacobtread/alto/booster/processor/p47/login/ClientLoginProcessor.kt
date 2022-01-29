package com.jacobtread.alto.booster.processor.p47.login

import com.jacobtread.alto.booster.packets.p47.login.server.SEnableCompressionPacket
import com.jacobtread.alto.booster.packets.p47.login.server.SEncryptionRequestPacket
import com.jacobtread.alto.booster.packets.p47.login.server.SLoginSuccessPacket
import com.jacobtread.alto.booster.processor.PacketProcessor
import com.jacobtread.alto.booster.processor.p47.DisconnectProcessor

interface ClientLoginProcessor : PacketProcessor, DisconnectProcessor {

    fun onLoginSuccess(packet: SLoginSuccessPacket)
    fun onEnableCompression(packet: SEnableCompressionPacket)
    fun onRequestEncryption(packet: SEncryptionRequestPacket)
}