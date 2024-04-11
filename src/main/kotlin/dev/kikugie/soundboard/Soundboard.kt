package dev.kikugie.soundboard

import dev.kikugie.soundboard.access.ApiAccess
import dev.kikugie.soundboard.audio.AudioCache
import dev.kikugie.soundboard.audio.AudioScheduler
import dev.kikugie.soundboard.gui.SoundBrowser
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.option.KeyBinding
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.isDirectory
import kotlin.io.path.notExists

object Soundboard : ClientModInitializer {
    const val MOD_ID = "soundboard"
    const val VERSION = "0.2.0"
    val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)
    val ROOT: Path = FabricLoader.getInstance().configDir.resolve(MOD_ID)
    lateinit var API: ApiAccess

    override fun onInitializeClient() {
        if (ROOT.notExists() || !ROOT.isDirectory())
            ROOT.createDirectory()
        val keybind = KeyBindingHelper.registerKeyBinding(
            KeyBinding("soundboard.browser.keybind", GLFW.GLFW_KEY_G, "soundboard.title")
        )
        ClientTickEvents.END_CLIENT_TICK.register {
            if (keybind.wasPressed()) SoundBrowser.open()
        }
    }

    fun id(path: String) = Identifier(MOD_ID, path)
}