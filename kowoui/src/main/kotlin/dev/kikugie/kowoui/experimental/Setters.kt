@file:Suppress("IMPLICIT_CAST_TO_ANY")

package dev.kikugie.kowoui.experimental

import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.GridLayout
import io.wispforest.owo.ui.container.StackLayout
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.ParentComponent

operator fun <P : ParentComponent, T : Component> P.plusAssign(component: T) = when (this) {
    is AppendableParentComponent -> addChild(component)
    is FlowLayout -> child(component)
    is StackLayout -> child(component)
    else -> error("Parent must implement AppendableParentComponent")
}.let { }

operator fun <P : ParentComponent, T : Component, I : Iterable<T>> P.plusAssign(components: I) = when (this) {
    is AppendableParentComponent -> addChildren(components)
    is FlowLayout -> children(components.toList())
    is StackLayout -> children(components.toList())
    else -> error("Parent must implement AppendableParentComponent")
}.let { }

operator fun <P : ParentComponent, T : Component> P.plus(component: T): P = when (this) {
    is AppendableParentComponent -> addChild(component)
    is FlowLayout -> child(component)
    is StackLayout -> child(component)
    else -> error("Parent must implement AppendableParentComponent")
}.let { this }

operator fun <P : ParentComponent, T : Component, I : Iterable<T>> P.plus(components: I) = when (this) {
    is AppendableParentComponent -> addChildren(components)
    is FlowLayout -> children(components.toList())
    is StackLayout -> children(components.toList())
    else -> error("Parent must implement AppendableParentComponent")
}.let { this }

fun FlowLayout.at(index: Int) = FlowLayoutSetter(index, this)
fun StackLayout.at(index: Int) = StackLayoutSetter(index, this)
fun GridLayout.at(row: Int, column: Int) = GridLayoutSetter(row, column, this)

class FlowLayoutSetter(private val index: Int, private val layout: FlowLayout) {
    operator fun <T : Component> plusAssign(component: T) {
        layout.child(index, component)
    }
}

class StackLayoutSetter(private val index: Int, private val layout: StackLayout) {
    operator fun <T : Component> plusAssign(component: T) {
        layout.child(index, component)
    }
}

class GridLayoutSetter(private val row: Int, private val column: Int, private val layout: GridLayout) {
    operator fun <T : Component> plusAssign(component: T) {
        layout.child(component, row, column)
    }
}