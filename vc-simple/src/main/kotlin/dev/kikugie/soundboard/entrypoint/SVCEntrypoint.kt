package dev.kikugie.soundboard.entrypoint

import de.maxhenkel.voicechat.api.VoicechatClientApi
import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.audiochannel.ClientAudioChannel
import de.maxhenkel.voicechat.api.events.ClientVoicechatConnectionEvent
import de.maxhenkel.voicechat.api.events.Event
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.MergeClientSoundEvent
import dev.kikugie.soundboard.MOD_ID
import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.audio.AudioScheduler
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient
import java.nio.file.Path
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED
import javax.sound.sampled.AudioInputStream

object SVCEntrypoint : SoundboardEntrypoint, VoicechatPlugin, ClientModInitializer {
    private var api: VoicechatClientApi? = null
    private var channel: ClientAudioChannel? = null
    override fun getPluginId() = MOD_ID
    override fun onInitializeClient() {
        SoundboardAccess.register(this)
        Soundboard.initialize()
    }

    override fun registerEvents(reg: EventRegistration) {
        reg.event<ClientVoicechatConnectionEvent> {
            api = it.voicechat
            connected = it.isConnected
            channel = api?.createStaticAudioChannel(MinecraftClient.getInstance().player!!.uuid)
            scheduler.reset()
        }
        reg.event<MergeClientSoundEvent> {
            val extra = scheduler.next() ?: return@event
            channel?.play(extra)
            it.mergeAudio(extra)
        }
    }

    private inline fun <reified T : Event> EventRegistration.event(priority: Int = 0, noinline action: (T) -> Unit) {
        registerEvent(T::class.java, action, priority)
    }

    override val format = AudioFormat(PCM_SIGNED, 48000F, 16, 1, 2, 48000F, false)
    override var connected = false
        private set
    override val scheduler: AudioScheduler = AudioScheduler(this)
}