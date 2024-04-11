package dev.kikugie.soundboard.access

import dev.kikugie.soundboard.access.Mods.Configuration.*
import javax.sound.sampled.AudioFormat

sealed interface ApiAccess {
    val frameSize: Int
    val audioFormat: AudioFormat

    fun init() {}

    companion object {
        val ACTIVE: ApiAccess
            get() = when (Mods.configuration) {
                SIMPLE_VC -> SimpleVoiceApiAccess
                PLASMO_VC -> PlasmoVoiceApiAccess
                NONE -> throw AssertionError("Loaded mod in no voicechat environment. How?")
            }.apply { init() }
    }
}