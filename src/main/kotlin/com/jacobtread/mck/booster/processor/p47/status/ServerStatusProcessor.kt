package com.jacobtread.mck.booster.processor.p47.status

import com.jacobtread.mck.booster.processor.p47.PingPongProcessor

interface ServerStatusProcessor : PingPongProcessor {

    fun onServerQuery()

}