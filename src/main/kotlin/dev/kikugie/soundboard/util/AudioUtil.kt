package dev.kikugie.soundboard.util

import dev.kikugie.soundboard.audio.data.SoundEntry
import java.io.BufferedInputStream
import java.io.InputStream
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.E
import kotlin.math.exp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

val Double.volumeScale get() =
    (exp(this) - 1) / (E - 1)

fun AudioFormat.duration(length: Int): Duration =
    duration(length.toLong())

fun AudioFormat.duration(length: Long): Duration =
    (length / sampleRate.toDouble()).seconds

fun AudioFormat.offset(duration: Duration): Int =
    (sampleRate * (duration.inWholeMicroseconds / 1_000_000.0)).toInt()

val AudioInputStream.duration: Duration
    get() = format.duration(frameLength)

fun InputStream.convert(format: AudioFormat): AudioInputStream? {
    val bufferedStream = BufferedInputStream(this)

    return try {
        // Try with standard AudioSystem (works for WAV and other supported formats)
        convert(AudioSystem.getAudioInputStream(bufferedStream), format)
    } catch (e: javax.sound.sampled.UnsupportedAudioFileException) {
        // Handle unsupported formats gracefully - return null instead of throwing
        bufferedStream.close()
        null
    }
}

fun SoundEntry.read(format: AudioFormat): ShortArray =
    supplier().use { inputStream ->
        val audioStream = inputStream.convert(format)
        if (audioStream == null) {
            // Return empty array for unsupported formats
            ShortArray(0)
        } else {
            val bytes = audioStream.readAllBytes()
            bytesToShorts(bytes)
        }
    }

fun convert(stream: AudioInputStream, targetFormat: AudioFormat): AudioInputStream {
    val originalFormat = stream.format
    val intermediateFormat = AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED,
        originalFormat.sampleRate,
        16,
        originalFormat.channels,
        originalFormat.channels * 2,
        originalFormat.sampleRate,
        false
    )
    return stream
        .let { AudioSystem.getAudioInputStream(intermediateFormat, it) }
        .let { AudioSystem.getAudioInputStream(targetFormat, it) }
}

fun bytesToShorts(array: ByteArray, length: Int = array.size / 2) = ShortArray(length) {
    val byte0 = array.getOrElse(it * 2) { 0 }.toInt() and 255
    val byte1 = array.getOrElse(it * 2 + 1) { 0 }.toInt() and 255
    (byte1 shl 8 or byte0).toShort()
}