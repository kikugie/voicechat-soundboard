package dev.kikugie.vcsoundboard

import de.maxhenkel.voicechat.api.VoicechatClientApi
import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.audiochannel.ClientAudioChannel
import de.maxhenkel.voicechat.api.events.ClientVoicechatConnectionEvent
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.MergeClientSoundEvent
import dev.kikugie.vcsoundboard.audio.AudioCache
import dev.kikugie.vcsoundboard.audio.AudioScheduler
import dev.kikugie.vcsoundboard.command.registerCommands
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import org.slf4j.LoggerFactory
import java.util.*

object VoiceChatSoundboard : VoicechatPlugin, ClientModInitializer {
    const val MOD_ID = "voicechat-soundboard"
    val LOGGER = LoggerFactory.getLogger(this::class.java)
    val ROOT = FabricLoader.getInstance().configDir.resolve(MOD_ID)

    val cache = AudioCache()
    val scheduler = AudioScheduler()

    lateinit var config: SoundboardConfig
        private set
    lateinit var channel: ClientAudioChannel
        private set
    lateinit var api: VoicechatClientApi
        private set

    override fun getPluginId() = MOD_ID

    override fun onInitializeClient() {
        config = SoundboardConfig()
        registerCommands()
    }

    override fun registerEvents(registration: EventRegistration) {
        registration.registerEvent(ClientVoicechatConnectionEvent::class.java) {
            api = it.voicechat
            channel = api.createEntityAudioChannel(MinecraftClient.getInstance().player!!.uuid)
            cache.clear()
        }
        registration.registerEvent(MergeClientSoundEvent::class.java) {
            scheduler.next()?.run {
                channel.play(this)
                it.mergeAudio(this)
            }
        }
    }
}