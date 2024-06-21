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
    var local: Boolean = false
        protected set

    abstract fun next(): ShortArray?
    abstract fun schedule(file: Path, local: Boolean, configuration: AudioConfiguration = AudioConfiguration.DEFAULT)
    abstract fun reset()

    companion object {
        fun loadAll(file: Path, entry: SoundboardEntrypoint): ShortArray =
            AudioSystem.getAudioInputStream(file.toFile()).use {
                    bytesToShorts(convert(it, entry.format).readAllBytes(), entry.frameSize)
                }

        @JvmStatic
        protected fun convert(stream: AudioInputStream, target: AudioFormat): AudioInputStream {
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
                .let { AudioSystem.getAudioInputStream(target, it) }
        }

        @JvmStatic
        protected fun bytesToShorts(array: ByteArray, frameSize: Int) = ShortArray(frameSize) {
            val byte0 = array.getOrElse(it * 2) { 0 }.toInt() and 255
            val byte1 = array.getOrElse(it * 2 + 1) { 0 }.toInt() and 255
            (byte1 shl 8 or byte0).toShort()
        }
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

    override fun schedule(file: Path, local: Boolean, configuration: AudioConfiguration) {
        reset()
        runBlocking {
            withContext(Dispatchers.IO) {
                mutex.withLock {
                    this@ArrayAudioScheduler.local = local
                    this@ArrayAudioScheduler.value = loadAll(file, entry)
                    this@ArrayAudioScheduler.file = file
                    this@ArrayAudioScheduler.configuration = configuration
                }
            }
        }
    }

    override fun reset() {
        local = false
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
        if (bytes.isEmpty()) null
        else bytesToShorts(bytes, entry.frameSize)
    } catch (e: IOException) {
        LOGGER.error("Failed to read data from $file", e)
        reset()
        null
    }

    override fun schedule(file: Path, local: Boolean, configuration: AudioConfiguration) {
        require(file.extension == "wav") { "Invalid file format $file. Only .wav files are supported" }
        reset()
        this.local = local
        this.file = file
        this.configuration = configuration
        this.input = convert(AudioSystem.getAudioInputStream(file.toFile()), entry.format)
    }

    override fun reset() {
        try {
            input?.close()
        } catch (e: IOException) {
            LOGGER.error("Failed to close $file", e)
        } finally {
            local = false
            file = null
            input = null
            configuration = null
        }
    }
}

enum class SchedulerType {
    ARRAY, STREAM;

    fun create(entry: SoundboardEntrypoint) = when (this) {
        ARRAY -> ArrayAudioScheduler(entry)
        STREAM -> StreamAudioScheduler(entry)
    }
}