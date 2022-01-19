package com.jacobtread.mck.booster.processor.p47.login

import com.jacobtread.mck.booster.packets.p47.login.server.SEnableCompressionPacket
import com.jacobtread.mck.booster.packets.p47.login.server.SEncryptionRequestPacket
import com.jacobtread.mck.booster.packets.p47.login.server.SLoginSuccessPacket
import com.jacobtread.mck.booster.processor.PacketProcessor
import com.jacobtread.mck.booster.processor.p47.DisconnectProcessor

interface ClientLoginProcessor : PacketProcessor, DisconnectProcessor {

    fun onLoginSuccess(packet: SLoginSuccessPacket)
    fun onEnableCompression(packet: SEnableCompressionPacket)
    fun onRequestEncryption(packet: SEncryptionRequestPacket)
}