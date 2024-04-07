package dev.kikugie.soundboard.access

import de.maxhenkel.voicechat.api.VoicechatClientApi
import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.audiochannel.ClientEntityAudioChannel
import de.maxhenkel.voicechat.api.events.ClientVoicechatConnectionEvent
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.MergeClientSoundEvent
import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.Soundboard.CACHE
import dev.kikugie.soundboard.Soundboard.SCHEDULER
import net.minecraft.client.MinecraftClient

object SimpleVoiceApiAccess : VoicechatPlugin, ApiAccess {
    private lateinit var client: VoicechatClientApi
    private lateinit var channel: ClientEntityAudioChannel

    override fun getPluginId() = Soundboard.MOD_ID


    override fun registerEvents(registration: EventRegistration) {
        registration.registerEvent(ClientVoicechatConnectionEvent::class.java) {
            client = it.voicechat
            channel = client.createEntityAudioChannel(MinecraftClient.getInstance().player!!.uuid)
            CACHE.clear()
        }
        registration.registerEvent(MergeClientSoundEvent::class.java) {
            SCHEDULER.next()?.run {
                channel.play(this)
                it.mergeAudio(this)
            }
        }
    }
}