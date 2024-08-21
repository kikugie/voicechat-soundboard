package dev.kikugie.kowoui.util

import io.wispforest.owo.ui.core.HorizontalAlignment
import io.wispforest.owo.ui.core.VerticalAlignment

enum class CombinedAlignment {
    TOP_LEFT, TOP_CENTER, TOP_RIGHT,
    CENTER_LEFT, CENTER, CENTER_RIGHT,
    BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT;

    val horizontal
        get() = when (this) {
            TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> HorizontalAlignment.LEFT
            TOP_CENTER, CENTER, BOTTOM_CENTER -> HorizontalAlignment.CENTER
            TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT -> HorizontalAlignment.RIGHT
        }

    val vertical
        get() = when (this) {
            TOP_LEFT, TOP_CENTER, TOP_RIGHT -> VerticalAlignment.TOP
            CENTER_LEFT, CENTER, CENTER_RIGHT -> VerticalAlignment.CENTER
            BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> VerticalAlignment.BOTTOM
        }

    companion object {
        fun of(horizontal: HorizontalAlignment, vertical: VerticalAlignment) = when (horizontal) {
            HorizontalAlignment.LEFT -> when (vertical) {
                VerticalAlignment.TOP -> TOP_LEFT
                VerticalAlignment.CENTER -> CENTER_LEFT
                VerticalAlignment.BOTTOM -> BOTTOM_LEFT
            }

            HorizontalAlignment.CENTER -> when (vertical) {
                VerticalAlignment.TOP -> TOP_CENTER
                VerticalAlignment.CENTER -> CENTER
                VerticalAlignment.BOTTOM -> BOTTOM_CENTER
            }

            HorizontalAlignment.RIGHT -> when (vertical) {
                VerticalAlignment.TOP -> TOP_RIGHT
                VerticalAlignment.CENTER -> CENTER_RIGHT
                VerticalAlignment.BOTTOM -> BOTTOM_RIGHT
            }
        }
    }
}