package dev.kikugie.soundboard.audio

import kotlin.math.min

class AudioScheduler {
    private var value: ShortArray? = null
    private var cursor: Int = 0

    fun schedule(data: ShortArray) {
        reset()
        value = data
    }

    fun next(): ShortArray? {
        if (value == null) return null
        val end = min(cursor + 960, value!!.size)
        val slice = ShortArray(960) { 0 }
        value!!.sliceArray(cursor until end).copyInto(slice)
        cursor += 960
        if (cursor >= value!!.size) reset()
        return slice
    }

    fun reset() {
        value = null
        cursor = 0
    }
}