package dev.kikugie.soundboard.gui.component

import dev.kikugie.soundboard.util.asText
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.core.OwoUIDrawContext
import net.minecraft.text.Text

abstract class DynamicTextComponent : LabelComponent(Text.empty()) {
    private var _string: String = ""
    abstract val string: String

    override fun draw(context: OwoUIDrawContext, mouseX: Int, mouseY: Int, partialTicks: Float, delta: Float) {
        // Epic coding
        if (_string != string) {
            text = string.asText()
            wrappedText = listOf(string.asText().asOrderedText())
            _string = string
        }
        super.draw(context, mouseX, mouseY, partialTicks, delta)
    }
}