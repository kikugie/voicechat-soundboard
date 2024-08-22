package dev.kikugie.soundboard.audio.data

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class AudioConfiguration(
    var start: Duration,
    var end: Duration,
    var volume: Double
): Cloneable {
    public override fun clone() = AudioConfiguration(start, end, volume)
    inline fun clone(block: AudioConfiguration.() -> Unit) = clone().apply(block)
    inline fun cloneOrNull(block: AudioConfiguration.() -> Unit) = clone(block).takeIf { it != DEFAULT }

    companion object {
        val DEFAULT = AudioConfiguration(Duration.ZERO, Duration.INFINITE, 1.0)
    }
}
