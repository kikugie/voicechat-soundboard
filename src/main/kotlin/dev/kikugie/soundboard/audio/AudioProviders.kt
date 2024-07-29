package dev.kikugie.soundboard.audio

import dev.kikugie.soundboard.util.*
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import kotlin.math.min

abstract class AudioProvider : AutoCloseable {
    abstract val configuration: AudioConfiguration
    abstract val format: AudioFormat
    abstract val until: Int
    abstract val volume: Double
    abstract var cursor: Int

    abstract fun next(samples: Int): ShortArray?

    protected inline fun AudioProvider.advance(samples: Int, consumer: (ShortArray, Int) -> Unit): ShortArray? {
        if (cursor >= until) return null
        val end = min((cursor + samples), until)
        val array = ShortArray(samples) { 0 }
        if (configuration.volume > 0) consumer(array, end)
        if (configuration.volume < 1) array.reassign {
            (it * volume).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        cursor += samples
        return array
    }
}

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