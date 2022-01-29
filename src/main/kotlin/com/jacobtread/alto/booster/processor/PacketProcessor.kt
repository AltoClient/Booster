package com.jacobtread.alto.booster.processor

import com.jacobtread.alto.booster.Packet
import com.jacobtread.alto.booster.ThreadQuickExitException
import com.jacobtread.alto.chat.Text
import com.jacobtread.alto.utils.thread.SingleThreadQueuedExecutor

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
