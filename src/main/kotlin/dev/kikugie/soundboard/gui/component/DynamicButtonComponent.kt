package dev.kikugie.soundboard.gui.component

import dev.kikugie.kowoui.text
import io.wispforest.owo.ui.component.ButtonComponent
import net.minecraft.text.Text

abstract class DynamicButtonComponent : ButtonComponent(Text.empty(), {}) {
    private var _text: Text = Text.empty()
    private var _string: String = ""
    abstract val string: String

    override fun getMessage(): Text {
        if (string != _string) {
            _string = string
            _text = string.text()
        }
        return _text
    }
}