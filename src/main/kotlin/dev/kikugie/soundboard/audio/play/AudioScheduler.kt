package dev.kikugie.soundboard.audio.play

import dev.kikugie.soundboard.entrypoint.SoundboardEntrypoint

class AudioScheduler(val entry: SoundboardEntrypoint) {
    val playing: Boolean
        get() = provider != null
    var local: Boolean = false
        private set
    var provider: AudioProvider? = null
        private set

    fun schedule(provider: AudioProvider, local: Boolean) {
        this.provider?.close()
        this.provider = provider
        this.local = local
    }

    fun next(): ShortArray? = provider?.next(entry.frameSize).also {
        if (it == null) reset()
    }

    fun reset() {
        provider?.close()
        provider = null
        local = false
    }
}