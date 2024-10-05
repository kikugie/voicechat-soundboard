package dev.kikugie.soundboard.audio.play

import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.audio.data.AudioConfiguration
import dev.kikugie.soundboard.util.*
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import kotlin.time.Duration

class StreamAudioProvider(
    private val input: AudioInputStream,
    override val configuration: AudioConfiguration,
) : AudioProvider() {
    override val format: AudioFormat = input.format
    override val until: Int = configuration.end.let {
        if (it == Duration.INFINITE) Int.MAX_VALUE
        else format.offset(it)
    }
    override val volume: Double = configuration.volume.coerceIn(0.0, Soundboard.config.volume).volumeScale
    override var cursor: Int = format.offset(configuration.start)

    init {
        input.skip((cursor * format.frameSize).toLong())
    }

    override fun next(samples: Int) = advance(samples) { array, end ->
        val bytes = input.readNBytes(samples * format.frameSize)
        if (bytes.size < array.size / 2) cursor = Int.MAX_VALUE
        bytesToShorts(bytes).copyInto(array)
    }

    override fun close() {
        input.close()
    }
}