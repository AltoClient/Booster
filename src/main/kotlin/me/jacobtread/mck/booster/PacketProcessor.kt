package me.jacobtread.mck.booster

import net.minecraft.util.IChatComponent
import net.minecraft.util.IThreadListener

interface PacketProcessor {

    fun disconnect(reason: IChatComponent)

    fun <V : PacketProcessor> ensureMainThread(threadListener: IThreadListener, packet: Packet<V>) {
        if (!threadListener.isCallingFromMinecraftThread) {
            @Suppress("UNCHECKED_CAST") val processor: V = this@PacketProcessor as V
            threadListener.addScheduledTask {
                packet.process(processor)
            }
            throw ThreadQuickExitException()
        }
    }
}
