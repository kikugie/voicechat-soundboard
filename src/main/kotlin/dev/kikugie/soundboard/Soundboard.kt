package dev.kikugie.soundboard

import dev.kikugie.soundboard.audio.SoundRegistry
import dev.kikugie.soundboard.config.AudioConfig
import dev.kikugie.soundboard.config.SoundboardConfig
import dev.kikugie.soundboard.entrypoint.SoundboardAccess
import dev.kikugie.soundboard.gui.screen.SoundBrowser
import dev.kikugie.soundboard.util.idOf
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resource.ResourceType
import org.lwjgl.glfw.GLFW
import kotlin.io.path.createDirectories

object Soundboard {
    private var ready = false
    val config = SoundboardConfig.load()

    fun initialize() {
        if (ready) return
        ready = true

        AudioConfig // Inits the object
        SoundRegistry.BASE_DIR.createDirectories()
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(SoundRegistry)

        ModKeyBinds.keybind(GLFW.GLFW_KEY_J, "browser") {
            inGame { SoundBrowser.open() }
            inGui { if (this is SoundBrowser) close() }
        }
        ModKeyBinds.keybind(GLFW.GLFW_KEY_U, "cancel") {
            val reset = { SoundboardAccess.forEach { scheduler.reset() } }
            inGame { reset() }
            inGui { reset() }
        }

        ResourceManagerHelper.registerBuiltinResourcePack(
            idOf("default"),
            FabricLoader.getInstance().getModContainer("soundboard-core").get(),
            ResourcePackActivationType.NORMAL
        )
    }
}