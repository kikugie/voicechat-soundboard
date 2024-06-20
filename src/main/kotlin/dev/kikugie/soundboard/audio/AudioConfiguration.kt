package dev.kikugie.soundboard.audio

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class AudioConfiguration(
    val start: Duration,
    val end: Duration,
    val volume: Float
) {
    companion object {
        val DEFAULT = AudioConfiguration(Duration.ZERO, Duration.INFINITE, 1F)
    }
}
