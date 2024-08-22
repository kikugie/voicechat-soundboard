package dev.kikugie.soundboard.audio.play

import dev.kikugie.soundboard.audio.data.AudioConfiguration
import dev.kikugie.soundboard.util.offset
import dev.kikugie.soundboard.util.volumeScale
import javax.sound.sampled.AudioFormat

class ArrayAudioProvider(
    private val data: ShortArray,
    override val format: AudioFormat,
    override val configuration: AudioConfiguration,
) : AudioProvider() {
    override val until = format.offset(configuration.end).coerceAtMost(data.size)
    override val volume: Double = configuration.volume.coerceIn(0.0, 1.0).volumeScale
    override var cursor: Int = format.offset(configuration.start)
    override fun next(samples: Int) = advance(samples) { array, end ->
        data.copyInto(array, 0, cursor, end)
    }

    override fun close() {}
}