package dev.kikugie.soundboard.access

import net.fabricmc.loader.api.FabricLoader

object Mods {
    private val fabric = FabricLoader.getInstance()
    val hasSimpleVoice: Boolean by lazy { fabric.isModLoaded("voicechat") }
    val hasPlasmoVoice: Boolean by lazy { fabric.isModLoaded("plasmovoice") }
}