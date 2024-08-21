package dev.kikugie.soundboard.gui.component

import dev.kikugie.soundboard.access.TextFieldAccessor
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.core.Sizing

open class ValidatableTextComponent : TextBoxComponent(Sizing.content()), TextFieldAccessor {
    override fun `soundboard$isValid`(text: String?): Boolean = `soundboard$predicate`().test(text)
}