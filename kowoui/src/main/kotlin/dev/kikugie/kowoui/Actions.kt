@file:Suppress("unused")

package dev.kikugie.kowoui

import io.wispforest.owo.ui.component.*
import io.wispforest.owo.ui.container.CollapsibleContainer
import io.wispforest.owo.ui.core.Color
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.Component.FocusSource
import io.wispforest.owo.ui.event.*

inline fun <T : Component> T.mouseDown(crossinline action: (Double, Double, Int) -> Boolean) =
    this.also { mouseDown().subscribe(MouseDown { x, y, b -> action(x, y, b) }) }

inline fun <T : Component> T.mouseUp(crossinline action: (Double, Double, Int) -> Boolean) =
    this.also { mouseUp().subscribe(MouseUp { x, y, b -> action(x, y, b) }) }

inline fun <T : Component> T.mouseScroll(crossinline action: (Double, Double, Double) -> Boolean) =
    this.also { mouseScroll().subscribe(MouseScroll { x, y, a -> action(x, y, a) }) }

inline fun <T : Component> T.mouseDrag(crossinline action: (Double, Double, Double, Double, Int) -> Boolean) =
    this.also { mouseDrag().subscribe(MouseDrag { x, y, dx, dy, b -> action(x, y, dx, dy, b) }) }

inline fun <T : Component> T.keyPress(crossinline action: (Int, Int, Int) -> Boolean) =
    this.also { keyPress().subscribe(KeyPress { k, c, m -> action(k, c, m) }) }

inline fun <T : Component> T.charTyped(crossinline action: (Char, Int) -> Boolean) =
    this.also { charTyped().subscribe(CharTyped { c, m -> action(c, m) }) }

inline fun <T : Component> T.mouseEnter(crossinline action: () -> Unit) =
    this.also { mouseEnter().subscribe(MouseEnter { action() }) }

inline fun <T : Component> T.mouseLeave(crossinline action: () -> Unit) =
    this.also { mouseLeave().subscribe(MouseLeave { action() }) }

inline fun <T : Component> T.focusGained(crossinline action: (FocusSource) -> Unit) =
    this.also { focusGained().subscribe(FocusGained { action(it) }) }

inline fun <T : Component> T.focusLost(crossinline action: () -> Unit) =
    this.also { focusLost().subscribe(FocusLost { action() }) }

inline fun <T : ColorPickerComponent> T.changed(crossinline action: (Color) -> Unit) =
    this.also { onChanged().subscribe(ColorPickerComponent.OnChanged { action(it) }) }

inline fun <T : CollapsibleContainer> T.toggled(crossinline action: (Boolean) -> Unit) =
    this.also { onToggled().subscribe(CollapsibleContainer.OnToggled { action(it) }) }

inline fun <T : SliderComponent> T.changed(crossinline action: (Double) -> Unit) =
    this.also { onChanged().subscribe(SliderComponent.OnChanged { action(it) }) }

inline fun <T : SliderComponent> T.slideEnded(crossinline action: () -> Unit) =
    this.also { slideEnd().subscribe(SliderComponent.OnSlideEnd { action() }) }

inline fun <T : SlimSliderComponent> T.changed(crossinline action: (Double) -> Unit) =
    this.also { onChanged().subscribe(SlimSliderComponent.OnChanged { action(it) }) }

inline fun <T : SlimSliderComponent> T.slideEnded(crossinline action: () -> Unit) =
    this.also { onSlideEnd().subscribe(SlimSliderComponent.OnSlideEnd { action() }) }

inline fun <T : SmallCheckboxComponent> T.changed(crossinline action: (Boolean) -> Unit) =
    this.also { onChanged().subscribe(SmallCheckboxComponent.OnChanged { action(it) }) }

inline fun <T : TextAreaComponent> T.changed(crossinline action: (String) -> Unit) =
    this.also { onChanged().subscribe(TextAreaComponent.OnChanged { action(it) }) }

inline fun <T : TextBoxComponent> T.changed(crossinline action: (String) -> Unit) =
    this.also { onChanged().subscribe(TextBoxComponent.OnChanged { action(it) }) }