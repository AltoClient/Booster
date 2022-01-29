package com.jacobtread.alto.booster


import com.jacobtread.alto.logger.Logger
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicInteger

class LanPingThread(val motd: String, val address: String) : Thread("LanPingThread #${THREAD_ID.incrementAndGet()}") {

    val socket = DatagramSocket()
    var running = true

    init {
        isDaemon = true
    }

    override fun run() {
        val response = getPingResponse(motd, address)
        val responseBytes = response.encodeToByteArray()
        while (!isInterrupted && running) {
            try {
                val address = InetAddress.getByName("224.0.2.60")
                val packet = DatagramPacket(responseBytes, responseBytes.size, address, 4445)
                socket.send(packet)
            } catch (e: IOException) {
                LOGGER.warn("Failed to send datagram", e)
                break
            }
            try {
                sleep(1500L)
            } catch (_: InterruptedException) {
            }
        }
    }

    override fun interrupt() {
        super.interrupt()
        running = false
    }

    companion object {
        private val LOGGER = Logger.get()
        private val THREAD_ID = AtomicInteger(0)

        fun getPingResponse(motd: String, address: String): String {
            return "[MOTD]$motd[/MOTD][AD]$address[/AD]"
        }

        fun getMotdFromPingResponse(text: String): String {
            val startIndex = text.indexOf("[MOTD]")
            return if (startIndex < 0) {
                "missing no"
            } else {
                val endIndex = text.indexOf("[/MOTD]", startIndex + "[MOTD]".length)
                if (endIndex < startIndex) "missing no" else text.substring(startIndex + "[MOTD]".length, endIndex)
            }
        }

        fun getAddrFromPingResponse(text: String): String? {
            val motdEnd = text.indexOf("[/MOTD]")
            return if (motdEnd < 0) {
                null
            } else {
                val motdEnd2 = text.indexOf("[/MOTD]", motdEnd + "[/MOTD]".length)
                if (motdEnd2 >= 0) {
                    null
                } else {
                    val startIndex = text.indexOf("[AD]", motdEnd + "[/MOTD]".length)
                    if (startIndex < 0) {
                        null
                    } else {
                        val endIndex = text.indexOf("[/AD]", startIndex + "[AD]".length)
                        if (endIndex < startIndex) null else text.substring(startIndex + "[AD]".length, endIndex)
                    }
                }
            }
        }
    }
}