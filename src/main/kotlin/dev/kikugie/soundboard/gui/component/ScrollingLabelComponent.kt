package dev.kikugie.soundboard.gui.component

import dev.kikugie.soundboard.util.client
import dev.kikugie.soundboard.util.drawScrollingText
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.core.OwoUIDrawContext
import net.minecraft.text.Text

class ScrollingLabelComponent(text: Text = Text.empty()) : LabelComponent(text) {
    private var center: (Int) -> Int = { x + width / 2 }
    fun center(func:  (Int) -> Int) {
        center = func
    }

    override fun draw(context: OwoUIDrawContext, mouseX: Int, mouseY: Int, partialTicks: Float, delta: Float) = with(context.matrices) {
        push()
        translate(0.0, 1 / client.window.scaleFactor, 0.0)
        context.drawScrollingText(
            textRenderer,
            text,
            center(textRenderer.getWidth(text)),
            x + margins.get().left,
            y + margins.get().top,
            x + width - margins.get().right,
            y + height - margins.get().bottom,
            color.get().argb(),
            shadow
        )
        pop()
    }
}