package dev.kikugie.soundboard

import dev.kikugie.soundboard.entrypoint.SoundboardAccess
import dev.kikugie.soundboard.gui.SoundBrowser
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import org.lwjgl.glfw.GLFW

object Soundboard {
    private var ready = false

    fun initialize() {
        if (ready) return
        ready = true

        keybind(GLFW.GLFW_KEY_J, "soundboard.keybinds.browser", SoundBrowser.Companion::open, SoundBrowser::close)
        keybind(GLFW.GLFW_KEY_U, "soundboard.keybinds.cancel", { SoundboardAccess.forEach { scheduler.reset() } }) {
            SoundboardAccess.forEach { scheduler.reset() }
        }
    }

    private inline fun keybind(
        default: Int,
        translation: String,
        crossinline inGame: () -> Unit,
        noinline inGui: (SoundBrowser) -> Unit,
    ) {
        val binding = KeyBindingHelper.registerKeyBinding(KeyBinding(translation, default, "soundboard.title"))
        ClientTickEvents.END_CLIENT_TICK.register {
            if (binding.wasPressed()) inGame()
        }
        SoundBrowser.keyAction(binding, inGui)
    }
}