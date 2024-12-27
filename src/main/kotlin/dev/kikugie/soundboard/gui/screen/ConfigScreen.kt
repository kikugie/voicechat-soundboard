package dev.kikugie.soundboard.gui.screen

import dev.kikugie.kowoui.access.alignment
import dev.kikugie.kowoui.access.gap
import dev.kikugie.kowoui.access.horizontalSizing
import dev.kikugie.kowoui.access.verticalSizing
import dev.kikugie.kowoui.experimental.plus
import dev.kikugie.kowoui.horizontalFlow
import dev.kikugie.kowoui.label
import dev.kikugie.kowoui.util.CombinedAlignment
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.Sizing.fill
import io.wispforest.owo.ui.core.Sizing.fixed
import net.minecraft.text.Text

class ConfigScreen : ModScreen() {
    companion object : ScreenManager(ConfigScreen::class) {
        const val COBALT_CONFIG = "" // TODO
    }

    override fun FlowLayout.configure() {
        TODO("Not yet implemented")
    }

    private fun labeledEntry(text: Text, component: Component): Component = horizontalFlow {
        gap = 2
        horizontalSizing = fill()
        verticalSizing = fixed(12)
        this + label(text) {
            alignment = CombinedAlignment.CENTER_LEFT
        }
        this + component.apply {
            alignment = CombinedAlignment.CENTER_RIGHT
        }
    }
}