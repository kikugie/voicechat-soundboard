package dev.kikugie.soundboard.entrypoint

import dev.kikugie.soundboard.audio.AudioScheduler
import java.nio.file.Path
import javax.sound.sampled.AudioFormat

interface SoundboardEntrypoint {
    val format: AudioFormat
    val connected: Boolean
    val frameSize get() = format.sampleRate.toInt() / 50

    val scheduler: AudioScheduler

    fun play(file: Path) {
        scheduler.schedule(file)
    }
}