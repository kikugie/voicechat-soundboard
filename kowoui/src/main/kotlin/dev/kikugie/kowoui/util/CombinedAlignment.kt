package dev.kikugie.kowoui.util

import io.wispforest.owo.ui.core.HorizontalAlignment
import io.wispforest.owo.ui.core.HorizontalAlignment.*
import io.wispforest.owo.ui.core.VerticalAlignment
import io.wispforest.owo.ui.core.VerticalAlignment.*

import io.wispforest.owo.ui.core.VerticalAlignment.CENTER as VCENTER
import io.wispforest.owo.ui.core.HorizontalAlignment.CENTER as HCENTER

enum class CombinedAlignment {
    TOP_LEFT, TOP_CENTER, TOP_RIGHT,
    CENTER_LEFT, CENTER, CENTER_RIGHT,
    BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT;

    val horizontal
        get() = when (this) {
            TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> LEFT
            TOP_CENTER, CENTER, BOTTOM_CENTER -> HCENTER
            TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT -> RIGHT
        }

    val vertical
        get() = when (this) {
            TOP_LEFT, TOP_CENTER, TOP_RIGHT -> TOP
            CENTER_LEFT, CENTER, CENTER_RIGHT -> VCENTER
            BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> BOTTOM
        }

    companion object {
        @JvmStatic
        fun of(horizontal: HorizontalAlignment, vertical: VerticalAlignment) = when (horizontal) {
            LEFT -> when (vertical) {
                TOP -> TOP_LEFT
                VCENTER -> CENTER_LEFT
                BOTTOM -> BOTTOM_LEFT
            }

            HCENTER -> when (vertical) {
                TOP -> TOP_CENTER
                VCENTER -> CENTER
                BOTTOM -> BOTTOM_CENTER
            }

            RIGHT -> when (vertical) {
                TOP -> TOP_RIGHT
                VCENTER -> CENTER_RIGHT
                BOTTOM -> BOTTOM_RIGHT
            }
        }
    }
}