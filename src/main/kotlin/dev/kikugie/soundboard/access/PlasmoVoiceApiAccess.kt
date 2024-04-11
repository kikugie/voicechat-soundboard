package dev.kikugie.soundboard.access

import dev.kikugie.soundboard.Soundboard
import su.plo.voice.api.addon.AddonInitializer
import su.plo.voice.api.addon.InjectPlasmoVoice
import su.plo.voice.api.addon.annotation.Addon
import su.plo.voice.api.client.PlasmoVoiceClient
import javax.sound.sampled.AudioFormat

@Addon(
    id = Soundboard.MOD_ID,
    version = Soundboard.VERSION,
    name = "Voice Chat Soundboard",
    authors = ["KikuGie"]
)
object PlasmoVoiceApiAccess : AddonInitializer, ApiAccess {
    @InjectPlasmoVoice
    private lateinit var client: PlasmoVoiceClient
    private val voiceInfo get() = client.serverInfo.get().voiceInfo

    override fun onAddonInitialize() {}

    override val frameSize: Int
        get() = voiceInfo.frameSize
    override val audioFormat: AudioFormat by lazy { voiceInfo.createFormat(false) }
    override fun init() {
        PlasmoVoiceClient.getAddonsLoader().load(this)
    }
}