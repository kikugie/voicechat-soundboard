package dev.kikugie.kowoui.experimental

import io.wispforest.owo.ui.core.Component

interface AppendableParentComponent {
    fun <C : Component> addChild(component: C)
    fun <C : Component> addChildren(components: Iterable<C>)
}