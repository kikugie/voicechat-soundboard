package dev.kikugie.soundboard.config

import dev.kikugie.soundboard.audio.data.AudioConfiguration
import dev.kikugie.soundboard.audio.data.SoundEntry
import dev.kikugie.soundboard.audio.play.ArrayAudioProvider
import dev.kikugie.soundboard.audio.play.AudioProvider
import dev.kikugie.soundboard.audio.play.StreamAudioProvider
import dev.kikugie.soundboard.util.convert
import dev.kikugie.soundboard.util.read
import javax.sound.sampled.AudioFormat

enum class AudioProviderType {
    STREAM {
        override fun create(
            entry: SoundEntry,
            format: AudioFormat,
            configuration: AudioConfiguration
        ) = StreamAudioProvider(entry.supplier().convert(format), configuration)
    },
    ARRAY {
        override fun create(
            entry: SoundEntry,
            format: AudioFormat,
            configuration: AudioConfiguration
        ) = ArrayAudioProvider(entry.read(format), format, configuration)
    };

    abstract fun create(
        entry: SoundEntry,
        format: AudioFormat,
        configuration: AudioConfiguration = AudioConfig[entry] ?: AudioConfiguration.DEFAULT
    ): AudioProvider
}