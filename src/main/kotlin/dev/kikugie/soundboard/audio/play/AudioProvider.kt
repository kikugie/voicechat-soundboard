package dev.kikugie.soundboard.audio.play

import dev.kikugie.soundboard.audio.data.AudioConfiguration
import dev.kikugie.soundboard.util.reassign
import javax.sound.sampled.AudioFormat
import kotlin.math.min

abstract class AudioProvider : AutoCloseable {
    abstract val configuration: AudioConfiguration
    abstract val format: AudioFormat
    abstract val until: Int
    abstract val volume: Double
    abstract var cursor: Int

    abstract fun next(samples: Int): ShortArray?

    protected inline fun advance(samples: Int, consumer: (ShortArray, Int) -> Unit): ShortArray? {
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