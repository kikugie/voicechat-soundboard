package dev.kikugie.soundboard.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

abstract class ScreenManager(private val cls: KClass<out Screen>) {
    fun open() = RenderSystem.recordRenderCall { MinecraftClient.getInstance().setScreen(cls.createInstance()) }
    fun close() = MinecraftClient.getInstance().currentScreen?.let { if (it::class == cls) it.close() }
}