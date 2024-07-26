package dev.kikugie.soundboard.util

import com.mojang.blaze3d.systems.RenderSystem
import io.wispforest.owo.ui.component.SlimSliderComponent
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.component.TextBoxComponent.OnChanged
import io.wispforest.owo.ui.container.CollapsibleContainer
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.StackLayout
import io.wispforest.owo.ui.core.Color
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.Component.FocusSource
import io.wispforest.owo.ui.core.OwoUIDrawContext
import io.wispforest.owo.ui.core.ParentComponent
import io.wispforest.owo.ui.event.*
import io.wispforest.owo.ui.inject.GreedyInputComponent
import io.wispforest.owo.ui.parsing.UIModel
import net.minecraft.client.render.*
import org.joml.Vector2d

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

inline fun TextBoxComponent.changed(crossinline action: (String) -> Unit) =
    this.also { onChanged().subscribe(OnChanged { action(it) }) }

inline fun SlimSliderComponent.ended(crossinline action: () -> Unit) =
    this.also { onSlideEnd().subscribe(SlimSliderComponent.OnSlideEnd { action() }) }

inline fun CollapsibleContainer.toggled(crossinline action: (Boolean) -> Unit) =
    this.also { onToggled().subscribe(CollapsibleContainer.OnToggled { action(it) }) }

fun <T : ParentComponent> T.all(): Sequence<Component> = sequence {
    for (it in children()) {
        if (it is GreedyInputComponent) continue
        if (it is ParentComponent) for (it1 in it.all())
            yield(it1)
        yield(it)
    }
    yield(this@all)
}

fun FlowLayout.children(vararg components: Component) : FlowLayout = children(components.toList())
fun StackLayout.children(vararg components: Component) : StackLayout = children(components.toList())

inline fun <reified T : Component> UIModel.template(
    name: String,
    params: Map<String, String> = emptyMap(),
): T = this.expandTemplate(T::class.java, name, params)

fun OwoUIDrawContext.drawLinePrecise(x1: Double, y1: Double, x2: Double, y2: Double, thickness: Double, color: Color) {
    val offset: Vector2d = Vector2d(x2 - x1, y2 - y1).perpendicular().normalize().mul(thickness * .5)

    val buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
    val matrix = matrices.peek().positionMatrix
    val vColor = color.argb()

    buffer.vertex(matrix, (x1 + offset.x).toFloat(), (y1 + offset.y).toFloat(), 0f).color(vColor)
    buffer.vertex(matrix, (x1 - offset.x).toFloat(), (y1 - offset.y).toFloat(), 0f).color(vColor)
    buffer.vertex(matrix, (x2 - offset.x).toFloat(), (y2 - offset.y).toFloat(), 0f).color(vColor)
    buffer.vertex(matrix, (x2 + offset.x).toFloat(), (y2 + offset.y).toFloat(), 0f).color(vColor)

    RenderSystem.enableBlend()
    RenderSystem.defaultBlendFunc()
    RenderSystem.setShader(GameRenderer::getPositionColorProgram)

    BufferRenderer.drawWithGlobalProgram(buffer.end())
}