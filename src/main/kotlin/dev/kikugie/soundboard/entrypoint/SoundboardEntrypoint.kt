package dev.kikugie.soundboard.entrypoint

import dev.kikugie.soundboard.audio.AudioScheduler
import dev.kikugie.soundboard.SoundRegistry
import javax.sound.sampled.AudioFormat

interface SoundboardEntrypoint {
    val format: AudioFormat
    val connected: Boolean
    val frameSize get() = format.sampleRate.toInt() / 50

    val scheduler: AudioScheduler
    val muted: Boolean

    fun play(local: Boolean, entry: SoundRegistry.SoundEntry) {
        if (connected) scheduler.schedule(local, entry)
    }
}