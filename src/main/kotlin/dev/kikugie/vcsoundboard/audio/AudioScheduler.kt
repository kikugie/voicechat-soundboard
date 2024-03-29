package dev.kikugie.vcsoundboard.audio

class AudioScheduler {
    var value: ShortArray? = null
        private set
    fun schedule(data: ShortArray?) {
        value = data
    }
}