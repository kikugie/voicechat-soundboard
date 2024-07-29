package dev.kikugie.soundboard.gui.component

import dev.kikugie.kowoui.text
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.core.OwoUIDrawContext
import net.minecraft.text.Text
import kotlin.properties.Delegates.observable

class DynamicTextComponent : LabelComponent(Text.empty()) {
    private var supplier: () -> String = {""}
    private var string: String by observable("") { _, old, new ->
        if (old == new) return@observable
        text = new.text()
        wrappedText = listOf(text.asOrderedText())
    }

    fun string(action: () -> String) {
        supplier = action
    }

    override fun draw(context: OwoUIDrawContext, mouseX: Int, mouseY: Int, partialTicks: Float, delta: Float) {
        string = supplier()
        super.draw(context, mouseX, mouseY, partialTicks, delta)
    }
}