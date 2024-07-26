package dev.kikugie.soundboard.audio

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class AudioConfiguration(
    var start: Duration,
    var end: Duration,
    var volume: Float
) {
    companion object {
        val DEFAULT = AudioConfiguration(Duration.ZERO, Duration.INFINITE, 1F)
    }
}
