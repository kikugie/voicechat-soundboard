package dev.kikugie.kowoui.dynamic

import dev.kikugie.kowoui.cached
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.core.OwoUIDrawContext
import net.minecraft.text.Text

class DynamicLabelComponent(initial: Text) : LabelComponent(Text.empty()) {
    private var provider: () -> Text = { initial }
    private var cache: Text by cached(initial) {
        text = it
        wrappedText = listOf(it.asOrderedText())
    }

    fun text(provider: () -> Text) = apply {
        this.provider = provider
    }

    override fun draw(context: OwoUIDrawContext?, mouseX: Int, mouseY: Int, partialTicks: Float, delta: Float) {
        cache = provider()
        super.draw(context, mouseX, mouseY, partialTicks, delta)
    }
}