package dev.kikugie.soundboard.audio

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class SoundId(val str: String)