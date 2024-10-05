package dev.kikugie.soundboard.audio.play

import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.audio.data.AudioConfiguration
import dev.kikugie.soundboard.util.offset
import dev.kikugie.soundboard.util.volumeScale
import javax.sound.sampled.AudioFormat
import kotlin.time.Duration

class ArrayAudioProvider(
    private val data: ShortArray,
    override val format: AudioFormat,
    override val configuration: AudioConfiguration,
) : AudioProvider() {
    override val until: Int = configuration.end.let {
        if (it == Duration.INFINITE) data.size
        else format.offset(it)
    }
    override val volume: Double = configuration.volume.coerceIn(0.0, Soundboard.config.volume).volumeScale
    override var cursor: Int = format.offset(configuration.start)
    override fun next(samples: Int) = advance(samples) { array, end ->
        data.copyInto(array, 0, cursor, end)
    }

    override fun close() {}
}