package dev.kikugie.soundboard.access

import dev.kikugie.soundboard.Soundboard
import su.plo.voice.api.addon.AddonInitializer
import su.plo.voice.api.addon.InjectPlasmoVoice
import su.plo.voice.api.addon.annotation.Addon
import su.plo.voice.api.client.PlasmoVoiceClient
import su.plo.voice.api.event.EventSubscribe

@Addon(
    id = Soundboard.MOD_ID,
    version = Soundboard.VERSION,
    name = "Voice Chat Soundboard",
    authors = ["KikuGie"]
)
object PlasmoVoiceApiAccess : AddonInitializer, ApiAccess {
    @InjectPlasmoVoice
    private lateinit var client: PlasmoVoiceClient

    override fun onAddonInitialize() {
    }

}