package dev.kikugie.soundboard.util

import io.wispforest.owo.ui.container.CollapsibleContainer
import io.wispforest.owo.ui.container.CollapsibleContainer.OnToggled
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.Component.FocusSource
import io.wispforest.owo.ui.core.ParentComponent
import io.wispforest.owo.ui.event.*
import io.wispforest.owo.ui.inject.GreedyInputComponent

inline fun <reified T : Component> ParentComponent.childById(id: String): T? = childById(T::class.java, id)

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

inline fun CollapsibleContainer.toggled(crossinline action: (Boolean) -> Unit) =
    this.also { onToggled().subscribe(OnToggled { action(it) }) }

fun <T : ParentComponent> T.all(): Sequence<Component> = sequence {
    for (it in children()) {
        if (it is GreedyInputComponent) continue
        if (it is ParentComponent) for (it1 in it.all())
            yield(it1)
    }
    yield(this@all)
}