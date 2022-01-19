package com.jacobtread.mck.booster

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.IOException
import java.net.*

class LanServerDetector {

    private val networkInterface: NetworkInterface? = NetworkInterface.getByIndex(0)
    private val broadcastAddress = InetSocketAddress(InetAddress.getByName("224.0.2.60"), 4445)
    val servers: ArrayList<LanServer> = ArrayList()
        @Synchronized get
    var wasUpdated = false
        @Synchronized get
        @Synchronized set

    fun addLanServer(pingResponse: String, address: InetAddress) {
        val motd = LanPingThread.getMotdFromPingResponse(pingResponse)
        var addr = LanPingThread.getAddrFromPingResponse(pingResponse)
        if (addr != null) {
            addr = address.hostAddress + ":" + addr
            var exists = false
            for (server in servers) {
                if (server.address == addr) {
                    server.lastSeen = System.nanoTime() / 1000000L
                    exists = true
                    break
                }
            }
            if (!exists) {
                servers.add(LanServer(motd, addr))
                wasUpdated = true

            }
        }
    }

    var lanFindThread: Thread? = null

    fun createFindThread() = Thread {
        val socket = MulticastSocket(4445).apply {
            soTimeout = 500
            joinGroup(broadcastAddress, networkInterface)
        }
        val data = ByteArray(1024)
        while (!Thread.interrupted()) {
            val packet = DatagramPacket(data, data.size)
            try {
                socket.receive(packet)
            } catch (e: SocketTimeoutException) {
                continue
            } catch (e: IOException) {
                LOGGER.error("Couldn't ping server", e)
                break
            }
            val response = String(packet.data, packet.offset, packet.length)
            if (LOGGER.isDebugEnabled) LOGGER.debug("${packet.address}:${response}")
            addLanServer(response, packet.address)
        }
        try {
            socket.leaveGroup(broadcastAddress, networkInterface)
        } catch (_: IOException) {
        }
        socket.close()
    }.apply {
        isDaemon = true
        name = "LanFindThread"
    }

    fun start() {
        lanFindThread = createFindThread().apply {
            start()
        }

    }

    fun stop() {
        lanFindThread?.interrupt()
        lanFindThread = null
    }

    fun restart() {
        servers.clear()
        if (lanFindThread != null) {
            stop()
        }
        start()
    }


    data class LanServer(val motd: String, val address: String) {
        var lastSeen = System.nanoTime() / 1000000L
    }

    companion object {
        val LOGGER: Logger = LogManager.getLogger()
    }
}