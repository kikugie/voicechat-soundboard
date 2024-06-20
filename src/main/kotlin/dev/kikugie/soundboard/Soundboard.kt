package dev.kikugie.soundboard

import dev.kikugie.soundboard.gui.SoundBrowser
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.option.KeyBinding
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory

object Soundboard {
    val LOGGER = LoggerFactory.getLogger(Soundboard::class.java)
    private var ready = false

    fun initialize() {
        if (ready) return
        ready = true

        keybind(GLFW.GLFW_KEY_J, "soundboard.browser.keybind", SoundBrowser.Companion::open, SoundBrowser::close)
    }

    private inline fun <T : Screen> keybind(default: Int, translation: String, crossinline inGame: () -> Unit, inGui: (T) -> Unit) {
        val binding = KeyBindingHelper.registerKeyBinding(KeyBinding(translation, default, "soundboard.title"))
        ClientTickEvents.END_CLIENT_TICK.register {
            if (binding.wasPressed()) inGame()
        }
    }
}