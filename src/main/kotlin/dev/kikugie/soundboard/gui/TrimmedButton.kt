package dev.kikugie.soundboard.gui

import dev.kikugie.soundboard.util.TEXT_RENDERER
import dev.kikugie.soundboard.util.asText
import dev.kikugie.soundboard.util.trimFancy
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.core.Size
import net.minecraft.text.Text

class TrimmedButton(private val original: Text) : ButtonComponent(Text.empty(), {}) {
    // FIXME: This should carry on the style
    override fun inflate(space: Size) {
        super.inflate(space)
        val available = horizontalSizing().get().inflate(space.width) {
            TEXT_RENDERER.getWidth(message) + 8
        }
        message = original.string.trimFancy(available - 8).asText()
    }

    companion object {
        fun from(other: ButtonComponent, text: Text = other.message) = TrimmedButton(text).apply {
            message = text
            onPress { other.onPress }
            renderer(other.renderer())
            textShadow(other.textShadow())
            active(other.active())
            cursorStyle(other.cursorStyle())
            positioning(other.positioning().get())
            margins(other.margins().get())
            horizontalSizing(other.horizontalSizing().get())
            verticalSizing(other.verticalSizing().get())
            tooltip(other.tooltip())
            zIndex(other.zIndex())
        }
    }
}