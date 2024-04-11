package dev.kikugie.soundboard.access

import de.maxhenkel.voicechat.api.VoicechatClientApi
import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.audiochannel.ClientEntityAudioChannel
import de.maxhenkel.voicechat.api.events.ClientVoicechatConnectionEvent
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.MergeClientSoundEvent
import dev.kikugie.soundboard.Soundboard
import net.minecraft.client.MinecraftClient
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED

object SimpleVoiceApiAccess : VoicechatPlugin, ApiAccess {
    private lateinit var client: VoicechatClientApi
    private lateinit var channel: ClientEntityAudioChannel

    override fun getPluginId() = Soundboard.MOD_ID

    override fun registerEvents(registration: EventRegistration) {
        registration.registerEvent(ClientVoicechatConnectionEvent::class.java) {
            client = it.voicechat
            channel = client.createEntityAudioChannel(MinecraftClient.getInstance().player!!.uuid)
        }
        registration.registerEvent(MergeClientSoundEvent::class.java) {

        }
    }

    override val frameSize: Int = 960
    override val audioFormat: AudioFormat = AudioFormat(PCM_SIGNED, 48000F, 16, 1, 2, 48000F, false)
}