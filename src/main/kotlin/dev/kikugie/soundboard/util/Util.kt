package dev.kikugie.soundboard.util

import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer

fun bytesToShorts(bytes: ByteArray): ShortArray {
    require(bytes.size % 2 == 0) { "Input bytes need to be divisible by 2" }
    val data = ShortArray(bytes.size / 2)

    var i = 0
    while (i < bytes.size) {
        data[i / 2] = bytesToShort(bytes[i], bytes[i + 1])
        i += 2
    }

    return data
}

fun bytesToShort(b1: Byte, b2: Byte): Short {
    return ((b2.toInt() and 255) shl 8 or (b1.toInt() and 255)).toShort()
}

fun shortsToBytes(shorts: ShortArray): ByteArray {
    val data = ByteArray(shorts.size * 2)

    for (i in shorts.indices) {
        val split: ByteArray = shortToBytes(shorts[i])
        data[i * 2] = split[0]
        data[i * 2 + 1] = split[1]
    }

    return data
}

fun shortToBytes(s: Short): ByteArray {
    return byteArrayOf((s.toInt() and 255).toByte(), (s.toInt() shr 8 and 255).toByte())
}