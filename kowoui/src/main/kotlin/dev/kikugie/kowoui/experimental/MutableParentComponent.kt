package dev.kikugie.kowoui.experimental

import io.wispforest.owo.ui.core.Component

interface MutableParentComponent<T> where T : Position {
    fun <C : Component> addChild(position: T, component: C)
    fun <C : Component> addChildren(position: T, components: Iterable<C>)
}