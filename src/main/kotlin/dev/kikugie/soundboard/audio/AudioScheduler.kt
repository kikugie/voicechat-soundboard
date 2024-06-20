package dev.kikugie.soundboard.audio

import dev.kikugie.soundboard.LOGGER
import dev.kikugie.soundboard.entrypoint.SoundboardEntrypoint
import java.io.IOException
import java.nio.file.Path
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.io.path.extension

class AudioScheduler(private val entry: SoundboardEntrypoint) {
    private var file: Path? = null
    private var input: AudioInputStream? = null
    var configuration: AudioConfiguration? = null
        private set
    val isActive get() = input != null
    fun next(): ShortArray? = if (input == null) null
    else try {
        val fullSize = entry.frameSize * 2
        val bytes = input!!.readNBytes(fullSize)
        if (bytes.size < fullSize) reset()
        bytesToShorts(bytes)
    } catch (e: IOException) {
        LOGGER.error("Failed to read data from $file", e)
        reset()
        null
    }

    fun schedule(file: Path, configuration: AudioConfiguration = AudioConfiguration.DEFAULT) {
        require(file.extension == "wav") { "Invalid file format $file. Only .wav files are supported" }
        reset()
        this.file = file
        this.configuration = configuration
        this.input = convert(AudioSystem.getAudioInputStream(file.toFile()))
    }

    fun reset() {
        try {
            input?.close()
        } catch (e: IOException) {
            LOGGER.error("Failed to close $file", e)
        } finally {
            file = null
            input = null
            configuration = null
        }
    }

    private fun convert(stream: AudioInputStream): AudioInputStream {
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
            .let { AudioSystem.getAudioInputStream(entry.format, it) }
    }

    private fun bytesToShorts(array: ByteArray) = ShortArray(entry.frameSize) {
        val byte0 = array.getOrElse(it * 2) { 0 }.toInt() and 255
        val byte1 = array.getOrElse(it * 2 + 1) { 0 }.toInt() and 255
        (byte0 shl 8 or byte1).toShort()
    }
}