package com.jacobtread.alto.booster

class ThreadQuickExitException : RuntimeException() {
    companion object {
        val INSTANCE: ThreadQuickExitException = ThreadQuickExitException()
    }
}