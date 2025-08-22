package dev.kikugie.soundboard.config

import dev.kikugie.soundboard.audio.data.AudioConfiguration
import dev.kikugie.soundboard.audio.data.SoundEntry
import dev.kikugie.soundboard.audio.play.ArrayAudioProvider
import dev.kikugie.soundboard.audio.play.AudioProvider
import dev.kikugie.soundboard.audio.play.Mp3SpiAudioProvider
import dev.kikugie.soundboard.audio.play.StreamAudioProvider
import dev.kikugie.soundboard.audio.getAudioExtension
import dev.kikugie.soundboard.util.convert
import dev.kikugie.soundboard.util.read
import javax.sound.sampled.AudioFormat

enum class AudioProviderType {
    STREAM {
        override fun create(
            entry: SoundEntry,
            format: AudioFormat,
            configuration: AudioConfiguration
        ): AudioProvider? = try {
            // Only use STREAM for WAV files
            if (entry.id.path.getAudioExtension() == "wav") {
                StreamAudioProvider(entry.supplier().convert(format), configuration)
            } else null
        } catch (e: Exception) {
            null
        }
    },

    ARRAY {
        override fun create(
            entry: SoundEntry,
            format: AudioFormat,
            configuration: AudioConfiguration
        ): AudioProvider? = try {
            // Only use ARRAY for WAV files
            if (entry.id.path.getAudioExtension() == "wav") {
                ArrayAudioProvider(entry.read(format), format, configuration)
            } else null
        } catch (e: Exception) {
            null
        }
    },

    MP3SPI {
        override fun create(
            entry: SoundEntry,
            format: AudioFormat,
            configuration: AudioConfiguration
        ): AudioProvider? = try {
            // Use MP3SPI for MP3 files and as fallback for other formats
            val extension = entry.id.path.getAudioExtension()
            if (extension == "mp3" || extension != "wav") {
                Mp3SpiAudioProvider(entry, format, configuration)
            } else null
        } catch (e: Exception) {
            null
        }
    };

    abstract fun create(
        entry: SoundEntry,
        format: AudioFormat,
        configuration: AudioConfiguration = AudioConfig[entry] ?: AudioConfiguration.DEFAULT
    ): AudioProvider?
}