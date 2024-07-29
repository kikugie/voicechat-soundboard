package dev.kikugie.soundboard.gui.component

import dev.kikugie.kowoui.text
import io.wispforest.owo.ui.component.ButtonComponent
import net.minecraft.text.Text
import kotlin.properties.Delegates.observable

class DynamicButtonComponent : ButtonComponent(Text.empty(), {}) {
    private var supplier: () -> String = {""}
    private var text: Text = Text.empty()
    var string: String by observable("") { _, old, new ->
        if (old != new) text = new.text()
    }

    fun string(action: () -> String) {
        supplier = action
        string = action()
    }

    override fun getMessage(): Text {
        string = supplier()
        return text
    }
}