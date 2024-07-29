package dev.kikugie.soundboard.gui.component

import dev.kikugie.soundboard.access.ValidatableField
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.core.Sizing

open class ValidatableTextComponent : TextBoxComponent(Sizing.content()), ValidatableField {
    override fun `soundboard$isValid`(text: String?): Boolean = textPredicate.test(text)
}