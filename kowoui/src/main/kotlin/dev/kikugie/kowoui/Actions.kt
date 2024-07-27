@file:Suppress("unused")

package dev.kikugie.kowoui

import io.wispforest.owo.ui.component.*
import io.wispforest.owo.ui.container.CollapsibleContainer
import io.wispforest.owo.ui.core.Color
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.Component.FocusSource
import io.wispforest.owo.ui.event.*

inline fun Component.mouseDown(crossinline action: (Double, Double, Int) -> Boolean) =
    this.also { mouseDown().subscribe(MouseDown { x, y, b -> action(x, y, b) }) }

inline fun Component.mouseUp(crossinline action: (Double, Double, Int) -> Boolean) =
    this.also { mouseUp().subscribe(MouseUp { x, y, b -> action(x, y, b) }) }

inline fun Component.mouseScroll(crossinline action: (Double, Double, Double) -> Boolean) =
    this.also { mouseScroll().subscribe(MouseScroll { x, y, a -> action(x, y, a) }) }

inline fun Component.mouseDrag(crossinline action: (Double, Double, Double, Double, Int) -> Boolean) =
    this.also { mouseDrag().subscribe(MouseDrag { x, y, dx, dy, b -> action(x, y, dx, dy, b) }) }

inline fun Component.keyPress(crossinline action: (Int, Int, Int) -> Boolean) =
    this.also { keyPress().subscribe(KeyPress { k, c, m -> action(k, c, m) }) }

inline fun Component.charTyped(crossinline action: (Char, Int) -> Boolean) =
    this.also { charTyped().subscribe(CharTyped { c, m -> action(c, m) }) }

inline fun Component.mouseEnter(crossinline action: () -> Unit) =
    this.also { mouseEnter().subscribe(MouseEnter { action() }) }

inline fun Component.mouseLeave(crossinline action: () -> Unit) =
    this.also { mouseLeave().subscribe(MouseLeave { action() }) }

inline fun Component.focusGained(crossinline action: (FocusSource) -> Unit) =
    this.also { focusGained().subscribe(FocusGained { action(it) }) }

inline fun Component.focusLost(crossinline action: () -> Unit) =
    this.also { focusLost().subscribe(FocusLost { action() }) }

inline fun ColorPickerComponent.changed(crossinline action: (Color) -> Unit) =
    this.also { onChanged().subscribe(ColorPickerComponent.OnChanged { action(it) }) }

inline fun CollapsibleContainer.toggled(crossinline action: (Boolean) -> Unit) =
    this.also { onToggled().subscribe(CollapsibleContainer.OnToggled { action(it) }) }

inline fun SliderComponent.changed(crossinline action: (Double) -> Unit) =
    this.also { onChanged().subscribe(SliderComponent.OnChanged { action(it) }) }

inline fun SliderComponent.slideEnded(crossinline action: () -> Unit) =
    this.also { slideEnd().subscribe(SliderComponent.OnSlideEnd { action() }) }

inline fun SlimSliderComponent.changed(crossinline action: (Double) -> Unit) =
    this.also { onChanged().subscribe(SlimSliderComponent.OnChanged { action(it) }) }

inline fun SlimSliderComponent.slideEnded(crossinline action: () -> Unit) =
    this.also { onSlideEnd().subscribe(SlimSliderComponent.OnSlideEnd { action() }) }

inline fun SmallCheckboxComponent.changed(crossinline action: (Boolean) -> Unit) =
    this.also { onChanged().subscribe(SmallCheckboxComponent.OnChanged { action(it) }) }

inline fun TextAreaComponent.changed(crossinline action: (String) -> Unit) =
    this.also { onChanged().subscribe(TextAreaComponent.OnChanged { action(it) }) }

inline fun TextBoxComponent.changed(crossinline action: (String) -> Unit) =
    this.also { onChanged().subscribe(TextBoxComponent.OnChanged { action(it) }) }