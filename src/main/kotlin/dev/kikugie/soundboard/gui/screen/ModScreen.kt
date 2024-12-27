package dev.kikugie.soundboard.gui.screen

import dev.kikugie.kowoui.access.*
import dev.kikugie.kowoui.experimental.plusAssign
import dev.kikugie.kowoui.horizontalFlow
import dev.kikugie.kowoui.stack
import dev.kikugie.kowoui.util.CombinedAlignment
import dev.kikugie.kowoui.verticalFlow
import dev.kikugie.soundboard.gui.CONFIG_PANEL
import dev.kikugie.soundboard.gui.widget.SidebarWidget
import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.StackLayout
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.Insets.both
import io.wispforest.owo.ui.core.Insets.of
import io.wispforest.owo.ui.core.OwoUIAdapter
import io.wispforest.owo.ui.core.Sizing.expand
import io.wispforest.owo.ui.core.Sizing.fill

abstract class ModScreen : BaseOwoScreen<StackLayout>() {
    protected lateinit var root: StackLayout
    override fun shouldPause(): Boolean = false
    override fun createAdapter(): OwoUIAdapter<StackLayout> = OwoUIAdapter.create(this) { h, v ->
        stack {
            horizontalSizing = h
            verticalSizing = v
            padding = both(60, 30)
            alignment = CombinedAlignment.CENTER
        }
    }

    override fun build(component: StackLayout) {
        root = component
        root += horizontalFlow {
            gap = 2
            horizontalSizing = fill()
            surface = CONFIG_PANEL
            padding = of(5)
            val container = verticalFlow {
                id = "container"
                horizontalSizing = expand()
            }
            this += setup(container)
            this += SidebarWidget(this@ModScreen)
            container.configure()
        }
    }

    protected open fun setup(container: FlowLayout): Component = container
    protected abstract fun FlowLayout.configure()
}