package dev.kikugie.soundboard

import dev.kikugie.soundboard.audio.SoundRegistry
import dev.kikugie.soundboard.config.AudioConfig
import dev.kikugie.soundboard.config.SoundboardConfig
import dev.kikugie.soundboard.entrypoint.SoundboardAccess
import dev.kikugie.soundboard.gui.SoundBrowser
import dev.kikugie.soundboard.util.idOf
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.option.KeyBinding
import net.minecraft.resource.ResourceType
import org.lwjgl.glfw.GLFW
import kotlin.io.path.createDirectories

object Soundboard {
    private var ready = false
    val config = SoundboardConfig.load()
    val keybinds: MutableMap<String, KeyBinding> = mutableMapOf()

    fun initialize() {
        if (ready) return
        ready = true

        AudioConfig // Inits the object
        SoundRegistry.BASE_DIR.createDirectories()
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(SoundRegistry)
        keybind(GLFW.GLFW_KEY_J, "browser", SoundBrowser.Companion::open) {
            SoundBrowser.keyAction(it) {
                if (settings != null) closeSettings()
                else close()
            }
        }
        keybind(GLFW.GLFW_KEY_U, "cancel", { SoundboardAccess.forEach { scheduler.reset() } }) {
            SoundBrowser.keyAction(it) { SoundboardAccess.forEach { scheduler.reset() } }
        }
//        keybind(GLFW.GLFW_KEY_I, "config", InGameConfig.Companion::open) {
//            InGameConfig.close()
//        }
        ResourceManagerHelper.registerBuiltinResourcePack(
            idOf("default"),
            FabricLoader.getInstance().getModContainer("soundboard-core").get(),
            ResourcePackActivationType.NORMAL
        )
    }

    private inline fun keybind(
        default: Int,
        name: String,
        crossinline inGame: () -> Unit,
        inGui: (KeyBinding) -> Unit,
    ) {
        val binding = KeyBindingHelper.registerKeyBinding(KeyBinding("soundboard.keybinds.$name", default, "soundboard.title"))
        ClientTickEvents.END_CLIENT_TICK.register {
            if (binding.wasPressed()) inGame()
        }
        inGui(binding)
        keybinds[name] = binding
    }
}