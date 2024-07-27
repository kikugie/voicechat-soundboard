package dev.kikugie.soundboard.entrypoint

import dev.kikugie.soundboard.audio.*
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

    fun scheduleArray(
        data: ShortArray,
        local: Boolean,
        configuration: AudioConfiguration,
    ) {
        val provider = ArrayAudioProvider(data, format, configuration)
        scheduler.schedule(provider, local)
    }

    fun scheduleArray(
        entry: SoundEntry,
        local: Boolean,
        configuration: AudioConfiguration = AudioConfig[entry] ?: AudioConfiguration.DEFAULT,
    ) = runOn(Dispatchers.IO) {
        val provider = ArrayAudioProvider(entry.read(format), format, configuration)
        scheduler.schedule(provider, local)
    }

    fun scheduleStream(
        entry: SoundEntry,
        local: Boolean,
        configuration: AudioConfiguration = AudioConfig[entry] ?: AudioConfiguration.DEFAULT,
    ) {
        val provider = StreamAudioProvider(entry.supplier().convert(format), configuration)
        scheduler.schedule(provider, local)
    }
}