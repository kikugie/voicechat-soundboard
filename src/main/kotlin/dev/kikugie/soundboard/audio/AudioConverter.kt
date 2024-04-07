package dev.kikugie.soundboard.audio

import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.util.bytesToShorts
import dev.kikugie.soundboard.util.shortsToBytes
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.file.Path
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.UnsupportedAudioFileException
import kotlin.io.path.inputStream
import kotlin.math.min
import kotlin.time.Duration

fun convert(path: Path, duration: Duration, volume: Float) = when (AudioType.match(path)) {
    AudioType.MP3 -> convertMp3(path, ConvertParameters(duration, volume))
    AudioType.WAV -> convertWav(path, ConvertParameters(duration, volume))
    null -> throw UnsupportedAudioFileException("Invalid audio file")
}

data class ConvertParameters(
    val duration: Duration,
    val volume: Float,
)

internal val MP3_MAGIC_BYTES = arrayOf(
    byteArrayOf(0xFF.toByte(), 0xFB.toByte()),
    byteArrayOf(0xFF.toByte(), 0xF3.toByte()),
    byteArrayOf(0xFF.toByte(), 0xF2.toByte()),
    byteArrayOf(0x49.toByte(), 0x44.toByte(), 0x33.toByte())
)
private val FORMAT = AudioFormat(PCM_SIGNED, 48000F, 16, 1, 2, 48000F, false)
private fun AudioInputStream.convert(params: ConvertParameters): ShortArray {
    val newFormat = AudioFormat(
        PCM_SIGNED, format.sampleRate, 16, format.channels, format.channels * 2, format.sampleRate, false
    )
    val stream = AudioSystem.getAudioInputStream(newFormat, this)
        .let { AudioSystem.getAudioInputStream(FORMAT, this) }
    val data = bytesToShorts(stream.readAllBytes())
    val newDuration = (FORMAT.sampleRate * params.duration.inWholeMilliseconds / 1000F).toInt()
    val newSize = min(data.size, newDuration)
    val newData = ShortArray(newSize)
    for (i in 0 until newSize)
        newData[i] = (data[i] * params.volume).toInt().toShort()
    return newData
}

private fun convertWav(path: Path, params: ConvertParameters) =
    AudioSystem.getAudioInputStream(path.toFile()).use { s -> s.convert(params) }

fun convertMp3(path: Path, params: ConvertParameters): ShortArray = try {
    val mp3Decoder = Soundboard.API.createMp3Decoder(path.inputStream())
        ?: throw IOException("Error creating mp3 decoder")
    val data = shortsToBytes(mp3Decoder.decode())
    val byteArrayInputStream = ByteArrayInputStream(data)
    val audioFormat = mp3Decoder.audioFormat
    val source =
        AudioInputStream(byteArrayInputStream, audioFormat, (data.size / audioFormat.frameSize).toLong())
    source.convert(params)
} catch (e: Exception) {
    AudioSystem.getAudioInputStream(path.toFile()).use { s -> s.convert(params) }
}
