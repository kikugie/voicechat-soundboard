package dev.kikugie.soundboard.util

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TranslatableTextContent
import net.minecraft.util.Language

val TEXT_RENDERER: TextRenderer get() = MinecraftClient.getInstance().textRenderer

fun String.asText(): Text = Text.of(this)
fun String.asTranslation(vararg args: String): Text = Text.translatable(this, *args)
val String.renderWidth get() = TEXT_RENDERER.getWidth(this)

fun String.trimFancy(limit: Int): String {
    val width = renderWidth
    if (width <= limit) return this
    val dotsWidth = "...".renderWidth
    if (limit <= dotsWidth) return "..."
    return TEXT_RENDERER.trimToWidth(this, limit - dotsWidth) + "..."
}
