package dev.kikugie.soundboard.audio.play

import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.audio.BASE_DIR
import dev.kikugie.soundboard.audio.SUPPORTED_FORMATS
import dev.kikugie.soundboard.audio.data.AudioConfiguration
import dev.kikugie.soundboard.audio.data.SoundEntry
import dev.kikugie.soundboard.audio.getAudioExtension
import dev.kikugie.soundboard.util.offset
import dev.kikugie.soundboard.util.volumeScale
import javax.sound.sampled.*
import java.io.BufferedInputStream
import java.io.IOException
import java.nio.file.Files
import kotlin.time.Duration

/**
 * Mp3SPI-based audio provider that uses the same approach as the AudioPlayer mod.
 * This properly decodes MP3 files using the mp3spi library for the soundboard system.
 */
class Mp3SpiAudioProvider(
    private val entry: SoundEntry,
    override val format: AudioFormat,
    override val configuration: AudioConfiguration,
) : AudioProvider() {

    companion object {
        val TARGET_FORMAT = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 48000F, 16, 1, 2, 48000F, false)
    }

    // Pre-loaded audio data for the soundboard
    private var audioData: ShortArray = ShortArray(0)
    private var dataReady = false

    override val until: Int get() = if (configuration.end == Duration.INFINITE) audioData.size else
        minOf(audioData.size, format.offset(configuration.end))
    override val volume: Double = configuration.volume.coerceIn(0.0, Soundboard.config.volume).volumeScale
    override var cursor: Int = format.offset(configuration.start)

    init {
        loadAudioData()
    }

    private fun loadAudioData() {
        try {
            // Construct the actual file path in the soundboard directory
            val fileName = entry.id.path.removePrefix("/")
            println("Looking for audio file: $fileName")

            // Try multiple strategies to find the file
            val fileToLoad = findAudioFile(fileName)

            if (fileToLoad == null) {
                println("Audio file not found after trying all strategies: $fileName")
                return
            }

            println("Loading audio file with Mp3SPI: ${fileToLoad}")

            // Determine audio type and convert accordingly
            val audioType = getAudioType(fileToLoad)
            when (audioType) {
                AudioType.WAV -> audioData = convertWav(fileToLoad)
                AudioType.MP3 -> audioData = convertMp3(fileToLoad)
                null -> {
                    println("Unsupported audio format for file: $fileToLoad")
                    return
                }
            }

            dataReady = true
            println("Loaded ${audioData.size} audio samples using Mp3SPI")

        } catch (e: Exception) {
            println("Failed to load audio file with Mp3SPI: ${e.message}")
            e.printStackTrace()
            audioData = ShortArray(0)
            dataReady = false
        }
    }

    private fun findAudioFile(fileName: String): java.nio.file.Path? {
        // Strategy 1: Try the exact filename as given
        val exactFile = BASE_DIR.resolve(fileName)
        if (Files.exists(exactFile)) {
            println("Found exact file: $exactFile")
            return exactFile
        }

        // Strategy 2: Try with common extensions if no extension present
        if (!fileName.contains('.')) {
            for (ext in SUPPORTED_FORMATS) {
                val fileWithExt = BASE_DIR.resolve("$fileName.$ext")
                if (Files.exists(fileWithExt)) {
                    println("Found file with extension: $fileWithExt")
                    return fileWithExt
                }
            }
        }

        // Strategy 3: Try replacing the extension with supported ones
        val baseFileName = if (fileName.contains('.')) {
            fileName.substringBeforeLast(".")
        } else {
            fileName
        }

        for (ext in SUPPORTED_FORMATS) {
            val fileWithNewExt = BASE_DIR.resolve("$baseFileName.$ext")
            if (Files.exists(fileWithNewExt)) {
                println("Found file with replaced extension: $fileWithNewExt")
                return fileWithNewExt
            }
        }

        // Strategy 4: List all files in directory and try fuzzy matching (including subdirectories)
        try {
            Files.walk(BASE_DIR).use { files ->
                val matchingFile = files.filter { Files.isRegularFile(it) }
                    .filter { file ->
                        val relativePath = BASE_DIR.relativize(file).toString().replace('\\', '/')
                        val name = file.fileName.toString()
                        // Try exact match first
                        relativePath == fileName ||
                        name == fileName ||
                        // Try without extension
                        relativePath.substringBeforeLast(".") == baseFileName ||
                        name.substringBeforeLast(".") == baseFileName ||
                        // Try case-insensitive match
                        relativePath.lowercase() == fileName.lowercase() ||
                        name.lowercase() == fileName.lowercase() ||
                        // Try fuzzy match (contains the base name)
                        relativePath.lowercase().contains(baseFileName.lowercase()) ||
                        name.lowercase().contains(baseFileName.lowercase())
                    }
                    .findFirst()
                    .orElse(null)

                if (matchingFile != null) {
                    println("Found file via fuzzy matching: $matchingFile")
                    return matchingFile
                }
            }
        } catch (e: Exception) {
            println("Error during file listing: ${e.message}")
        }

        return null
    }

    private fun getAudioType(path: java.nio.file.Path): AudioType? {
        return try {
            // First try by file extension
            val extension = path.toString().lowercase()
            when {
                extension.endsWith(".wav") -> AudioType.WAV
                extension.endsWith(".mp3") -> AudioType.MP3
                else -> {
                    // Fallback: try to detect by reading the file
                    Files.newInputStream(path).use { inputStream ->
                        if (isWav(inputStream)) {
                            AudioType.WAV
                        } else if (isMp3File(inputStream)) {
                            AudioType.MP3
                        } else {
                            // If detection fails but extension suggests MP3, try MP3 anyway
                            if (extension.endsWith(".mp3")) {
                                println("Audio format detection failed for MP3 file, but trying MP3 conversion anyway")
                                AudioType.MP3
                            } else {
                                null
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("Error detecting audio type: ${e.message}")
            // Fallback to extension-based detection
            val extension = path.toString().lowercase()
            when {
                extension.endsWith(".wav") -> AudioType.WAV
                extension.endsWith(".mp3") -> AudioType.MP3
                else -> null
            }
        }
    }

    private fun isWav(inputStream: java.io.InputStream): Boolean {
        return try {
            BufferedInputStream(inputStream).use { bis ->
                val fileFormat = AudioSystem.getAudioFileFormat(bis)
                fileFormat.type.toString().equals("wave", ignoreCase = true)
            }
        } catch (e: UnsupportedAudioFileException) {
            false
        }
    }

    private fun isMp3File(inputStream: java.io.InputStream): Boolean {
        return try {
            BufferedInputStream(inputStream).use { bis ->
                val fileFormat = AudioSystem.getAudioFileFormat(bis)
                fileFormat.type.toString().equals("mp3", ignoreCase = true)
            }
        } catch (e: UnsupportedAudioFileException) {
            false
        }
    }

    private fun convertWav(file: java.nio.file.Path): ShortArray {
        AudioSystem.getAudioInputStream(file.toFile()).use { source ->
            return convert(source)
        }
    }

    private fun convertMp3(file: java.nio.file.Path): ShortArray {
        return try {
            // Try using the Java Sound API with mp3spi
            AudioSystem.getAudioInputStream(file.toFile()).use { source ->
                convert(source)
            }
        } catch (e: Exception) {
            println("Failed to convert MP3 file: ${e.message}")
            throw e
        }
    }

    private fun convert(source: AudioInputStream): ShortArray {
        val sourceFormat = source.format
        println("Source format: $sourceFormat")

        try {
            // Step 1: Convert to PCM if needed
            val pcmStream = if (sourceFormat.encoding != AudioFormat.Encoding.PCM_SIGNED) {
                val pcmFormat = AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    sourceFormat.sampleRate,
                    16,
                    sourceFormat.channels,
                    sourceFormat.channels * 2,
                    sourceFormat.sampleRate,
                    false
                )
                println("Converting to PCM format: $pcmFormat")
                AudioSystem.getAudioInputStream(pcmFormat, source)
            } else {
                source
            }

            // Step 2: Read all data from PCM stream
            val allBytes = pcmStream.readAllBytes()
            println("Read ${allBytes.size} bytes from audio stream")

            // Step 3: Convert to target format (resample and convert to mono if needed)
            val pcmFormat = pcmStream.format
            val shorts = if (pcmFormat.channels == 1) {
                // Already mono, just convert bytes to shorts
                bytesToShorts(allBytes)
            } else {
                // Convert stereo to mono by averaging channels
                stereoToMono(allBytes, pcmFormat)
            }

            // Step 4: Resample to 48kHz if needed
            return if (pcmFormat.sampleRate != 48000f) {
                resample(shorts, pcmFormat.sampleRate, 48000f)
            } else {
                shorts
            }

        } catch (e: Exception) {
            println("Error during audio conversion: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    private fun stereoToMono(audioBytes: ByteArray, format: AudioFormat): ShortArray {
        val stereoShorts = bytesToShorts(audioBytes)
        val monoShorts = ShortArray(stereoShorts.size / 2)

        for (i in monoShorts.indices) {
            val left = stereoShorts[i * 2]
            val right = stereoShorts[i * 2 + 1]
            // Average the two channels
            monoShorts[i] = ((left.toInt() + right.toInt()) / 2).toShort()
        }

        println("Converted stereo (${stereoShorts.size} samples) to mono (${monoShorts.size} samples)")
        return monoShorts
    }

    private fun resample(samples: ShortArray, fromRate: Float, toRate: Float): ShortArray {
        if (fromRate == toRate) return samples

        val ratio = fromRate / toRate
        val newSize = (samples.size / ratio).toInt()
        val resampled = ShortArray(newSize)

        for (i in resampled.indices) {
            val sourceIndex = (i * ratio).toInt()
            if (sourceIndex < samples.size) {
                resampled[i] = samples[sourceIndex]
            }
        }

        println("Resampled from ${samples.size} samples @ ${fromRate}Hz to ${resampled.size} samples @ ${toRate}Hz")
        return resampled
    }

    private fun bytesToShorts(audioBytes: ByteArray): ShortArray {
        val shorts = ShortArray(audioBytes.size / 2)
        for (i in shorts.indices) {
            val low = audioBytes[i * 2].toInt() and 0xFF
            val high = (audioBytes[i * 2 + 1].toInt() and 0xFF) shl 8
            shorts[i] = (low or high).toShort()
        }
        return shorts
    }

    override fun next(samples: Int): ShortArray? {
        if (!dataReady) {
            return null
        }

        if (cursor >= until) return null
        val end = kotlin.math.min(cursor + samples, until)
        val out = ShortArray(samples)
        val toCopy = end - cursor
        if (toCopy > 0) {
            audioData.copyInto(out, 0, cursor, cursor + toCopy)
            // apply volume scaling safely
            val scale = if (configuration.volume <= 0.0) 1.0 else volume
            if (scale != 1.0) {
                var i = 0
                while (i < toCopy) {
                    val v = (out[i] * scale).toInt()
                    out[i] = v.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    i++
                }
            }
            // tail silence if fewer than requested
            if (toCopy < samples) java.util.Arrays.fill(out, toCopy, samples, 0)
        }
        cursor = end
        return out
    }

    override fun close() {
        // Nothing to close for static audio data
    }

    enum class AudioType(val extension: String) {
        MP3("mp3"),
        WAV("wav");

        fun isValidFileName(path: java.nio.file.Path): Boolean {
            return path.toString().lowercase().endsWith(".$extension")
        }
    }
}
