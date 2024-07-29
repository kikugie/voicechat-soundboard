@file:Suppress("unused")

package dev.kikugie.kowoui

import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.GridLayout
import io.wispforest.owo.ui.container.StackLayout
import io.wispforest.owo.ui.core.Component

val end: Nothing? = null

fun FlowLayout.at(index: Int? = end): ComponentSetter =
    FlowComponentSetter(this, index)

fun StackLayout.at(index: Int? = end): ComponentSetter =
    StackComponentSetter(this, index)

fun GridLayout.at(row: Int, column: Int): ComponentSetter =
    GridComponentSetter(this, row, column)

inline fun <T : Component> FlowLayout.child(build: () -> T): T =
    build().also { this@child.child(it) }

inline fun <T : Component> StackLayout.child(build: () -> T): T =
    build().also { this@child.child(it) }

inline fun <T : Component> FlowLayout.child(index: Int, build: () -> T): T =
    build().also { this@child.child(index, it) }

inline fun <T : Component> StackLayout.child(index: Int, build: () -> T): T =
    build().also { this@child.child(index, it) }

inline fun <T : Component> GridLayout.child(row: Int, column: Int, build: () -> T): T =
    build().also { this@child.child(it, row, column) }

// Different name because of type inference
fun <T : Component> GridLayout.setChild(row: Int, column: Int, child: T): T =
    child.also { this@setChild.child(it, row, column) }

fun FlowLayout.children(vararg components: Component): FlowLayout =
    children(components.toList())

fun StackLayout.children(vararg components: Component): StackLayout =
    children(components.toList())

fun FlowLayout.children(index: Int, vararg components: Component): FlowLayout =
    children(index, components.toList())

fun StackLayout.children(index: Int, vararg components: Component): StackLayout =
    children(index, components.toList())

interface ComponentSetter {
    infix fun <T : Component> set(component: T): T
}

class FlowComponentSetter(
    private val container: FlowLayout,
    private val index: Int?,
) : ComponentSetter {
    override fun <T : Component> set(component: T): T = component.also {
        if (index == null) container.child(component)
        else container.child(index, component)
    }
}

class StackComponentSetter(
    private val container: StackLayout,
    private val index: Int?,
) : ComponentSetter {
    override fun <T : Component> set(component: T): T = component.also {
        if (index == null) container.child(component)
        else container.child(index, component)
    }
}

class GridComponentSetter(
    private val container: GridLayout,
    private val row: Int,
    private val column: Int,
) : ComponentSetter {
    override fun <T : Component> set(component: T): T = component.also {
        container.child(component, row, column)
    }
}