package me.jacobtread.mck.booster

class ThreadQuickExitException : RuntimeException() {
    companion object {
        val INSTANCE: ThreadQuickExitException = ThreadQuickExitException()
    }
}