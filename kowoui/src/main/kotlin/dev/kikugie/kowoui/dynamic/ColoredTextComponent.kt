package dev.kikugie.kowoui.dynamic

import dev.kikugie.kowoui.mixinstuff.TextFieldAccessor
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.core.Color
import io.wispforest.owo.ui.core.Sizing

open class ColoredTextComponent : TextBoxComponent(Sizing.fill()), TextFieldAccessor {
    private var colorProvider: (String) -> Color? = { if (!`soundboard$predicate`().test(text)) Color.RED else null }

    override fun `soundboard$color`(text: String): Color? = colorProvider(text)
    fun color(provider: (String) -> Color?) {
        colorProvider = provider
    }
}