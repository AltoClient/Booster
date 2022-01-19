package com.jacobtread.mck.booster.processor

import com.jacobtread.mck.booster.Packet
import com.jacobtread.mck.booster.ThreadQuickExitException
import com.jacobtread.mck.chat.Text
import com.jacobtread.mck.utils.thread.SingleThreadQueuedExecutor

interface PacketProcessor {

    fun disconnect(reason: Text)

    fun <V : PacketProcessor> ensureMainThread(threadListener: SingleThreadQueuedExecutor, packet: Packet<V>) {
        if (!threadListener.isOnThread) {
            @Suppress("UNCHECKED_CAST") val processor: V = this@PacketProcessor as V
            threadListener.run(Runnable {
                packet.process(processor)
            })
            throw ThreadQuickExitException()
        }
    }
}
