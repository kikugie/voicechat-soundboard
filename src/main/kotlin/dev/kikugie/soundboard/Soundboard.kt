package dev.kikugie.soundboard

import de.maxhenkel.voicechat.api.VoicechatClientApi
import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.audiochannel.ClientAudioChannel
import de.maxhenkel.voicechat.api.events.ClientVoicechatConnectionEvent
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.MergeClientSoundEvent
import dev.kikugie.soundboard.audio.AudioCache
import dev.kikugie.soundboard.audio.AudioScheduler
import dev.kikugie.soundboard.gui.SoundBrowser
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.isDirectory
import kotlin.io.path.notExists

object Soundboard : VoicechatPlugin, ClientModInitializer {
    const val MOD_ID = "soundboard"
    val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)
    val ROOT: Path = FabricLoader.getInstance().configDir.resolve(MOD_ID)

    val CACHE = AudioCache()
    val SCHEDULER = AudioScheduler()

    lateinit var API: VoicechatClientApi
        private set
    private lateinit var channel: ClientAudioChannel

    override fun getPluginId() = MOD_ID

    override fun onInitializeClient() {
        if (ROOT.notExists() || !ROOT.isDirectory())
            ROOT.createDirectory()
        val keybind = KeyBindingHelper.registerKeyBinding(
            KeyBinding("soundboard.browser.keybind", GLFW.GLFW_KEY_H, "soundboard.title")
        )
        ClientTickEvents.END_CLIENT_TICK.register {
            if (keybind.wasPressed()) SoundBrowser.open()
        }
    }

    override fun registerEvents(registration: EventRegistration) {
        registration.registerEvent(ClientVoicechatConnectionEvent::class.java) {
            API = it.voicechat
            channel = API.createEntityAudioChannel(MinecraftClient.getInstance().player!!.uuid)
            CACHE.clear()
        }
        registration.registerEvent(MergeClientSoundEvent::class.java) {
            SCHEDULER.next()?.run {
                channel.play(this)
                it.mergeAudio(this)
            }
        }
    }

    fun play(file: Path, local: Boolean = false) =
        CACHE[file]?.let { play(it, local) }

    fun play(sound: ShortArray, local: Boolean = false) =
        if (local) channel.play(sound)
        else SCHEDULER.schedule(sound)

    fun id(path: String) = Identifier(MOD_ID, path)
}