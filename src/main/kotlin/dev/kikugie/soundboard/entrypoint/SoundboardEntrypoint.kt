package dev.kikugie.soundboard.entrypoint

import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.audio.data.AudioConfiguration
import dev.kikugie.soundboard.audio.data.SoundEntry
import dev.kikugie.soundboard.audio.play.ArrayAudioProvider
import dev.kikugie.soundboard.audio.play.AudioProvider
import dev.kikugie.soundboard.audio.play.AudioScheduler
import dev.kikugie.soundboard.audio.play.StreamAudioProvider
import dev.kikugie.soundboard.config.AudioConfig
import dev.kikugie.soundboard.util.convert
import dev.kikugie.soundboard.util.read
import dev.kikugie.soundboard.util.runOn
import kotlinx.coroutines.Dispatchers
import javax.sound.sampled.AudioFormat

interface SoundboardEntrypoint {
    val format: AudioFormat
    val connected: Boolean
    val frameSize get() = format.sampleRate.toInt() / 50

    val scheduler: AudioScheduler
    val muted: Boolean

    fun schedule(entry: SoundEntry, local: Boolean) =
        schedule(Soundboard.config.provider.create(entry, format), local)
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