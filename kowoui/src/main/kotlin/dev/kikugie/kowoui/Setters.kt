package dev.kikugie.kowoui

import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.GridLayout
import io.wispforest.owo.ui.container.StackLayout
import io.wispforest.owo.ui.core.Component

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