package dev.kikugie.soundboard.access

import dev.kikugie.soundboard.access.Mods.Configuration.*
import net.fabricmc.loader.api.FabricLoader

object Mods {
    private val fabric = FabricLoader.getInstance()
    val hasSimpleVoice: Boolean = fabric.isModLoaded("voicechat")
    val hasPlasmoVoice: Boolean = fabric.isModLoaded("plasmovoice")
    val configuration = when {
        hasSimpleVoice -> SIMPLE_VC
        hasPlasmoVoice -> PLASMO_VC
        else -> NONE
    }

    enum class Configuration { SIMPLE_VC, PLASMO_VC, NONE }
}