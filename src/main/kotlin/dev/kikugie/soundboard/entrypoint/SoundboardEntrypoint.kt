package dev.kikugie.soundboard.entrypoint

import dev.kikugie.soundboard.audio.data.AudioConfiguration
import dev.kikugie.soundboard.audio.data.SoundEntry
import dev.kikugie.soundboard.audio.play.ArrayAudioProvider
import dev.kikugie.soundboard.audio.play.AudioProvider
import dev.kikugie.soundboard.audio.play.AudioScheduler
import dev.kikugie.soundboard.config.AudioProviderType
import dev.kikugie.soundboard.config.AudioConfig
import javax.sound.sampled.AudioFormat

interface SoundboardEntrypoint {
    val format: AudioFormat
    val connected: Boolean
    val frameSize get() = format.sampleRate.toInt() / 50

    val scheduler: AudioScheduler
    val muted: Boolean

    fun schedule(entry: SoundEntry, local: Boolean) {
        val configuration = AudioConfig[entry] ?: entry.settings ?: AudioConfiguration.DEFAULT

        // Try each provider type in order of preference
        val provider = AudioProviderType.MP3SPI.create(entry, format, configuration)
            ?: AudioProviderType.STREAM.create(entry, format, configuration)
            ?: AudioProviderType.ARRAY.create(entry, format, configuration)

        if (provider != null) {
            schedule(provider, local)
        } else {
            println("Failed to play audio file: ${entry.id}. Format not supported.")
        }
    }

    fun schedule(provider: AudioProvider, local: Boolean) =
        scheduler.schedule(provider, local)

    fun scheduleArray(
        data: ShortArray,
        local: Boolean,
        configuration: AudioConfiguration,
    ) {
        val provider = ArrayAudioProvider(data, format, configuration)
        scheduler.schedule(provider, local)
    }
}