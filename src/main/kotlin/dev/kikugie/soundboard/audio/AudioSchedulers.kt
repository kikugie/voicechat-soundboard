package dev.kikugie.soundboard.audio

import dev.kikugie.soundboard.LOGGER
import dev.kikugie.soundboard.entrypoint.SoundboardEntrypoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.file.Path
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.io.path.extension
import kotlin.math.min

abstract class AudioScheduler {
    protected abstract val entry: SoundboardEntrypoint
    protected var file: Path? = null
    var configuration: AudioConfiguration? = null
        protected set

    abstract fun next(): ShortArray?
    abstract fun schedule(file: Path, configuration: AudioConfiguration = AudioConfiguration.DEFAULT)
    abstract fun reset()

    protected fun convert(stream: AudioInputStream): AudioInputStream {
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

    protected fun bytesToShorts(array: ByteArray) = ShortArray(entry.frameSize) {
        val byte0 = array.getOrElse(it * 2) { 0 }.toInt() and 255
        val byte1 = array.getOrElse(it * 2 + 1) { 0 }.toInt() and 255
        (byte1 shl 8 or byte0).toShort()
    }
}

class ArrayAudioScheduler(override val entry: SoundboardEntrypoint) : AudioScheduler() {
    private val mutex = Mutex()
    private var cursor: Int = 0
    private var value: ShortArray? = null

    override fun next(): ShortArray? {
        if (value == null) return null
        val end = min(cursor + 960, value!!.size)
        val slice = ShortArray(960) { 0 }
        value!!.sliceArray(cursor until end).copyInto(slice)
        cursor += 960
        if (cursor >= value!!.size) reset()
        return slice
    }

    override fun schedule(file: Path, configuration: AudioConfiguration) {
        reset()
        runBlocking {
            withContext(Dispatchers.IO) {
                mutex.withLock {
                    this@ArrayAudioScheduler.value = AudioSystem.getAudioInputStream(file.toFile()).use { bytesToShorts(convert(it).readAllBytes()) }
                    this@ArrayAudioScheduler.file = file
                    this@ArrayAudioScheduler.configuration = configuration
                }
            }
        }
    }

    override fun reset() {
        value = null
        cursor = 0
    }
}

class StreamAudioScheduler(override val entry: SoundboardEntrypoint) : AudioScheduler() {
    private var input: AudioInputStream? = null
    override fun next(): ShortArray? = if (input == null) null
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

    override fun schedule(file: Path, configuration: AudioConfiguration) {
        require(file.extension == "wav") { "Invalid file format $file. Only .wav files are supported" }
        reset()
        this.file = file
        this.configuration = configuration
        this.input = convert(AudioSystem.getAudioInputStream(file.toFile()))
    }

    override fun reset() {
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
}

enum class SchedulerType {
    ARRAY, STREAM;

    fun create(entry: SoundboardEntrypoint) = when(this) {
        ARRAY -> ArrayAudioScheduler(entry)
        STREAM -> StreamAudioScheduler(entry)
    }
}