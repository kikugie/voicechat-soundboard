@file:Suppress("unused")

package dev.kikugie.kowoui

import io.wispforest.owo.ui.component.*
import io.wispforest.owo.ui.container.CollapsibleContainer
import io.wispforest.owo.ui.core.Color
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.Component.FocusSource
import io.wispforest.owo.ui.event.*

fun <T : SlimSliderComponent> T.onChange(action: (Double) -> Unit) =
    this.also { onChanged().subscribe(SlimSliderComponent.OnChanged(action)) }

fun <T : ColorPickerComponent> T.onChange(action: (Color) -> Unit) =
    this.also { onChanged().subscribe(ColorPickerComponent.OnChanged(action)) }

fun <T : SmallCheckboxComponent> T.onChange(action: (Boolean) -> Unit) =
    this.also { onChanged().subscribe(SmallCheckboxComponent.OnChanged(action)) }

fun <T : SliderComponent> T.onChange(action: (Double) -> Unit) =
    this.also { onChanged().subscribe(SliderComponent.OnChanged(action)) }

fun <T : TextAreaComponent> T.onChange(action: (String) -> Unit) =
    this.also { onChanged().subscribe(TextAreaComponent.OnChanged(action)) }

fun <T : TextBoxComponent> T.onChange(action: (String) -> Unit) =
    this.also { onChanged().subscribe(TextBoxComponent.OnChanged(action)) }

fun <T : Component> T.onCharType(action: (Char, Int) -> Boolean) =
    this.also { charTyped().subscribe(CharTyped(action)) }

fun <T : Component> T.onFocusGain(action: (FocusSource) -> Unit) =
    this.also { focusGained().subscribe(FocusGained(action)) }

fun <T : Component> T.onFocusLose(action: () -> Unit) =
    this.also { focusLost().subscribe(FocusLost(action)) }

fun <T : Component> T.onKeyPress(action: (Int, Int, Int) -> Boolean) =
    this.also { keyPress().subscribe(KeyPress(action)) }

fun <T : Component> T.onMouseDown(action: (Double, Double, Int) -> Boolean) =
    this.also { mouseDown().subscribe(MouseDown(action)) }

fun <T : Component> T.onMouseDrag(action: (Double, Double, Double, Double, Int) -> Boolean) =
    this.also { mouseDrag().subscribe(MouseDrag(action)) }

fun <T : Component> T.onMouseEnter(action: () -> Unit) =
    this.also { mouseEnter().subscribe(MouseEnter(action)) }

fun <T : Component> T.onMouseLeave(action: () -> Unit) =
    this.also { mouseLeave().subscribe(MouseLeave(action)) }

fun <T : Component> T.onMouseScroll(action: (Double, Double, Double) -> Boolean) =
    this.also { mouseScroll().subscribe(MouseScroll(action)) }

fun <T : Component> T.onMouseUp(action: (Double, Double, Int) -> Boolean) =
    this.also { mouseUp().subscribe(MouseUp(action)) }

fun <T : SliderComponent> T.onSlideEnd(action: () -> Unit) =
    this.also { slideEnd().subscribe(SliderComponent.OnSlideEnd(action)) }

fun <T : SlimSliderComponent> T.onSlideEnd(action: () -> Unit) =
    this.also { onSlideEnd().subscribe(SlimSliderComponent.OnSlideEnd(action)) }

fun <T : CollapsibleContainer> T.onToggle(action: (Boolean) -> Unit) =
    this.also { onToggled().subscribe(CollapsibleContainer.OnToggled(action)) }
