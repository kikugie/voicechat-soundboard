package dev.kikugie.soundboard.audio

import dev.kikugie.soundboard.util.bytesToShorts
import dev.kikugie.soundboard.util.duration
import dev.kikugie.soundboard.util.offset
import dev.kikugie.soundboard.util.reassign
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import kotlin.math.min

interface AudioProvider : AutoCloseable {
    val configuration: AudioConfiguration
    fun next(samples: Int): ShortArray?
}

class ArrayAudioProvider(
    private val data: ShortArray,
    format: AudioFormat,
    override val configuration: AudioConfiguration,
) : AudioProvider {
    private val until = format.offset(configuration.end).coerceAtMost(data.size)
    private var cursor: Int = format.offset(configuration.start)
    override fun next(samples: Int): ShortArray? {
        if (cursor >= until) return null
        val end = min((cursor + samples), until)
        val array = ShortArray(samples) { 0 }
        if (configuration.volume > 0)
            data.copyInto(array, 0, cursor, end)
        if (configuration.volume < 1)
            array.reassign { (it * configuration.volume).toInt().toShort() }
        cursor += samples
        return array
    }

    override fun close() {}
}

class StreamAudioProvider(
    private val input: AudioInputStream,
    override val configuration: AudioConfiguration,
) : AudioProvider {
    private val format = input.format
    private val duration = input.duration
    private val until = format.offset(configuration.end).coerceAtMost(format.offset(duration))
    private var cursor: Int = format.offset(configuration.start)

    init {
        input.skip((cursor * format.frameSize).toLong())
    }

    override fun next(samples: Int): ShortArray? {
        if (cursor >= until) return null
        val end = min((cursor + samples), until)
        val array = ShortArray(samples) { 0 }
        if (configuration.volume > 0)
            bytesToShorts(input.readNBytes((end - cursor) * format.frameSize)).copyInto(array)
        if (configuration.volume < 1)
            array.reassign { (it * configuration.volume).toInt().toShort() }
        cursor += samples
        return array
    }

    override fun close() {
        input.close()
    }
}