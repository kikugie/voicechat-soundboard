package dev.kikugie.soundboard.audio.play

import dev.kikugie.soundboard.audio.data.AudioConfiguration
import dev.kikugie.soundboard.util.*
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream

class StreamAudioProvider(
    private val input: AudioInputStream,
    override val configuration: AudioConfiguration,
) : AudioProvider() {
    private val duration = input.duration
    override val format: AudioFormat = input.format
    override val until = format.offset(configuration.end).coerceAtMost(format.offset(duration))
    override val volume: Double = configuration.volume.coerceIn(0.0, 1.0).volumeScale
    override var cursor: Int = format.offset(configuration.start)

    init {
        input.skip((cursor * format.frameSize).toLong())
    }

    override fun next(samples: Int) = advance(samples) { array, end ->
        bytesToShorts(input.readNBytes((end - cursor) * format.frameSize)).copyInto(array)
    }

    override fun close() {
        input.close()
    }
}