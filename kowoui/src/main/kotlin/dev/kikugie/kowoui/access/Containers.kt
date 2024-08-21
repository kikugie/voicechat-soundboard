@file:Suppress("unused")

package dev.kikugie.kowoui.access

import io.wispforest.owo.ui.container.CollapsibleContainer
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.OverlayContainer
import io.wispforest.owo.ui.container.ScrollContainer

var OverlayContainer<*>.closeOnClick: Boolean
    get() = closeOnClick()
    set(value) {
        closeOnClick(value)
    }

var CollapsibleContainer.expanded: Boolean
    get() = expanded()
    set(value) {
        if (expanded != value) toggleExpansion()
    }

var ScrollContainer<*>.fixedScrollbarLength: Int
    get() = fixedScrollbarLength()
    set(value) {
        fixedScrollbarLength(value)
    }

var ScrollContainer<*>.scrollbarThickness: Int
    get() = scrollbarThiccness()
    set(value) {
        scrollbarThiccness(value)
    }

var FlowLayout.gap: Int
    get() = gap()
    set(value) {
        gap(value)
    }

var ScrollContainer<*>.scrollStep: Int
    get() = scrollStep()
    set(value) {
        scrollStep(value)
    }

var ScrollContainer<*>.scrollbar: ScrollContainer.Scrollbar
    get() = scrollbar()
    set(value) {
        scrollbar(value)
    }
