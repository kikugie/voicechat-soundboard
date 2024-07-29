package dev.kikugie.soundboard.gui.component

import dev.kikugie.kowoui.text
import io.wispforest.owo.ui.component.ButtonComponent
import net.minecraft.text.Text
import kotlin.properties.Delegates.observable

class DynamicButtonComponent : ButtonComponent(Text.empty(), {}) {
    var string: String by observable("") { _, old, new ->
        if (old != new) message = new.text()
    }
}