@file:Suppress("unused")

package dev.kikugie.kowoui.access

import dev.kikugie.kowoui.util.CombinedAlignment
import io.wispforest.owo.ui.core.*

var ParentComponent.allowOverflow: Boolean
    get() = allowOverflow()
    set(value) {
        allowOverflow(value)
    }

var ParentComponent.padding: Insets
    get() = padding().get()
    set(value) {
        padding(value)
    }

var ParentComponent.surface: Surface
    get() = surface()
    set(value) {
        surface(value)
    }

var ParentComponent.alignment: CombinedAlignment
    get() = CombinedAlignment.of(horizontalAlignment, verticalAlignment)
    set(value) {
        horizontalAlignment = value.horizontal
        verticalAlignment = value.vertical
    }

var ParentComponent.horizontalAlignment: HorizontalAlignment
    get() = horizontalAlignment()
    set(value) {
        horizontalAlignment(value)
    }

var ParentComponent.verticalAlignment: VerticalAlignment
    get() = verticalAlignment()
    set(value) {
        verticalAlignment(value)
    }

val ParentComponent.animatablePadding: AnimatableProperty<Insets>
    get() = padding()
val ParentComponent.children: List<Component>
    get() = children()