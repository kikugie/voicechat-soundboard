@file:Suppress("unused")

package dev.kikugie.kowoui.access

import dev.kikugie.kowoui.unsupported
import io.wispforest.owo.ui.core.*
import io.wispforest.owo.ui.util.FocusHandler
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.text.Text

var Component.cursorStyle: CursorStyle
    get() = cursorStyle()
    set(value) {
        cursorStyle(value)
    }

var Component.horizontalSizing: Sizing
    get() = horizontalSizing().get()
    set(value) {
        horizontalSizing(value)
    }

var Component.id: String?
    get() = id()
    set(value) {
        id(value)
    }

var Component.margins: Insets
    get() = margins().get()
    set(value) {
        margins(value)
    }

var Component.positioning: Positioning
    get() = positioning().get()
    set(value) {
        positioning(value)
    }

var Component.sizing: Sizing
    @Deprecated("Getter unavailable", level = DeprecationLevel.ERROR)
    get() = unsupported { "Getter unavailable" }
    set(value) {
        sizing(value)
    }

var Component.tooltip: List<TooltipComponent>?
    get() = tooltip()
    set(value) {
        tooltip(value)
    }

var Component.tooltipText: Text
    @Deprecated("Getter unavailable", level = DeprecationLevel.ERROR)
    get() = unsupported { "Getter unavailable" }
    set(value) {
        tooltip(value)
    }

var Component.tooltipTexts: Collection<Text>
    @Deprecated("Getter unavailable", level = DeprecationLevel.ERROR)
    get() = unsupported { "Getter unavailable" }
    set(value) {
        tooltip(value)
    }

var Component.verticalSizing: Sizing
    get() = verticalSizing().get()
    set(value) {
        verticalSizing(value)
    }

var Component.zIndex: Int
    get() = zIndex()
    set(value) {
        zIndex(value)
    }

val Component.animatableHorizontalSizing: AnimatableProperty<Sizing>
    get() = horizontalSizing()
val Component.animatableMargins: AnimatableProperty<Insets>
    get() = margins()
val Component.animatablePositioning: AnimatableProperty<Positioning>
    get() = positioning()
val Component.animatableVerticalSizing: AnimatableProperty<Sizing>
    get() = verticalSizing()
val Component.focusHandler: FocusHandler?
    get() = focusHandler()
val Component.hasParent: Boolean
    get() = hasParent()
val Component.parent: ParentComponent?
    get() = parent()
val Component.root: ParentComponent?
    get() = root()
