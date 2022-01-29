package com.jacobtread.alto.booster

import io.netty.util.AttributeKey

object Constants {
    val PROTOCOL_STATE_KEY: AttributeKey<ProtocolState> =
        AttributeKey.valueOf("protocol")

}