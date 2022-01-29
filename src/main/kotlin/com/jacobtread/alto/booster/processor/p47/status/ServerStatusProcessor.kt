package com.jacobtread.alto.booster.processor.p47.status

import com.jacobtread.alto.booster.processor.p47.PingPongProcessor

interface ServerStatusProcessor : PingPongProcessor {

    fun onServerQuery()

}