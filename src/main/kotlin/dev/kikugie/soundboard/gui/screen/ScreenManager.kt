package dev.kikugie.soundboard.gui.screen

import com.mojang.blaze3d.systems.RenderSystem
import dev.kikugie.soundboard.util.currentScreen
import net.minecraft.client.gui.screen.Screen
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

abstract class ScreenManager(private val cls: KClass<out Screen>) {
    fun open() = RenderSystem.recordRenderCall { currentScreen = cls.createInstance() }
    fun close() = currentScreen?.let { if (it::class == cls) it.close() }
}