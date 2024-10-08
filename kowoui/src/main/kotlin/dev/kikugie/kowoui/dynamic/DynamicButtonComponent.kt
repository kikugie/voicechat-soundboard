package dev.kikugie.kowoui.dynamic

import dev.kikugie.kowoui.cached
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.core.OwoUIDrawContext
import net.minecraft.text.Text

class DynamicButtonComponent(initial: Text) : ButtonComponent(initial, {}) {
    private var provider: (() -> Text)? = null
    private var cache: Text by cached(initial) {
        message = it
    }

    fun text(provider: () -> Text) = apply {
        this.provider = provider
    }

    override fun getMessage(): Text {
        provider?.invoke()?.let { cache = it }
        return super.getMessage()
    }
}