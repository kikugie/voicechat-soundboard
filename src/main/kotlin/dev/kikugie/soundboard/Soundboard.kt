package dev.kikugie.soundboard

import dev.kikugie.soundboard.config.SoundboardConfig
import dev.kikugie.soundboard.entrypoint.SoundboardAccess
import dev.kikugie.soundboard.gui.InGameConfig
import dev.kikugie.soundboard.gui.SoundBrowser
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import org.lwjgl.glfw.GLFW

object Soundboard {
    private var ready = false
    val config = SoundboardConfig.load()

    fun initialize() {
        if (ready) return
        ready = true

        keybind(GLFW.GLFW_KEY_J, "browser", SoundBrowser.Companion::open) {
            SoundBrowser.close()
        }
        keybind(GLFW.GLFW_KEY_U, "cancel", { SoundboardAccess.forEach { scheduler.reset() } }) {
            SoundBrowser.keyAction(it) {SoundboardAccess.forEach { scheduler.reset() }}
        }
//        keybind(GLFW.GLFW_KEY_I, "config", InGameConfig.Companion::open) {
//            InGameConfig.close()
//        }
    }

    private inline fun keybind(
        default: Int,
        name: String,
        crossinline inGame: () -> Unit,
        inGui: (KeyBinding) -> Unit = {  },
    ) {
        val binding = KeyBindingHelper.registerKeyBinding(KeyBinding("soundboard.keybinds.$name", default, "soundboard.title"))
        ClientTickEvents.END_CLIENT_TICK.register {
            if (binding.wasPressed()) inGame()
        }
        inGui(binding)
    }
}