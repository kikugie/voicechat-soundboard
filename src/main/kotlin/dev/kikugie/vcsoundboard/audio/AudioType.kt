package dev.kikugie.vcsoundboard.audio

import java.nio.file.Path
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import kotlin.io.path.extension
import kotlin.io.path.inputStream

enum class AudioType(val extension: String) {
    MP3("mp3"),
    WAV("wav");

    fun isValid(path: Path): Boolean {
        return path.extension == extension
    }

    companion object {
        fun match(extension: String): AudioType? = when (extension) {
            "mp3" -> MP3
            "wav" -> WAV
            else -> null
        }

        fun match(path: Path): AudioType? {
            if (WAV.isValid(path)) AudioSystem.getAudioInputStream(path.toFile()).use { ais ->
                if (isWav(ais.format)) return WAV
            } else if (MP3.isValid(path))
                if (isMp3File(path)) return MP3
            return null
        }

        fun isWav(audioFormat: AudioFormat): Boolean {
            val encoding = audioFormat.encoding
            return encoding == AudioFormat.Encoding.PCM_SIGNED || encoding == AudioFormat.Encoding.PCM_UNSIGNED || encoding == AudioFormat.Encoding.PCM_FLOAT || encoding == AudioFormat.Encoding.ALAW || encoding == AudioFormat.Encoding.ULAW
        }

        fun isMp3File(path: Path): Boolean {
            path.inputStream().use {
                return hasMp3MagicBytes(it.readNBytes(3))
            }
        }

        private fun hasMp3MagicBytes(data: ByteArray): Boolean {
            for (magicBytes in MP3_MAGIC_BYTES) {
                if (data.size < magicBytes.size) {
                    return false
                }
                var valid = true
                for (i in magicBytes.indices) {
                    if (data[i] != magicBytes[i]) {
                        valid = false
                        break
                    }
                }
                if (valid) {
                    return true
                }
            }
            return false
        }
    }
}