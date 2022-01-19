package me.jacobtread.mck.booster

import me.jacobtread.mck.chat.Text
import me.jacobtread.mck.utils.thread.SingleThreadQueuedExecutor

interface PacketProcessor {

    fun disconnect(reason: Text)

    fun <V : PacketProcessor> ensureMainThread(threadListener: SingleThreadQueuedExecutor, packet: Packet<V>) {
        if (!threadListener.isOnThread) {
            @Suppress("UNCHECKED_CAST") val processor: V = this@PacketProcessor as V
            threadListener.submit {
                packet.process(processor)
            }
            throw ThreadQuickExitException()
        }
    }
}
