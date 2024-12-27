package dev.kikugie.soundboard.util

import io.wispforest.owo.ui.core.Color
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.OwoUIDrawContext
import io.wispforest.owo.ui.core.ParentComponent
import io.wispforest.owo.ui.inject.GreedyInputComponent
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.text.Text
import net.minecraft.util.Util
import net.minecraft.util.math.MathHelper
import org.joml.Vector2d
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

fun <T : ParentComponent> T.all(): Sequence<Component> = sequence {
    for (it in children()) {
        if (it is GreedyInputComponent) continue
        if (it is ParentComponent) for (it1 in it.all())
            yield(it1)
        yield(it)
    }
    yield(this@all)
}

fun OwoUIDrawContext.drawLinePrecise(x1: Double, y1: Double, x2: Double, y2: Double, thickness: Double, color: Color) {
    val offset: Vector2d = Vector2d(x2 - x1, y2 - y1).perpendicular().normalize().mul(thickness * .5)

    val buffer = vertexConsumers().getBuffer(RenderLayer.getGui())
    val matrix = matrices.peek().positionMatrix
    val vColor = color.argb()

    buffer.vertex(matrix, (x1 + offset.x).toFloat(), (y1 + offset.y).toFloat(), 0f).color(vColor)
    buffer.vertex(matrix, (x1 - offset.x).toFloat(), (y1 - offset.y).toFloat(), 0f).color(vColor)
    buffer.vertex(matrix, (x2 - offset.x).toFloat(), (y2 - offset.y).toFloat(), 0f).color(vColor)
    buffer.vertex(matrix, (x2 + offset.x).toFloat(), (y2 + offset.y).toFloat(), 0f).color(vColor)
}

fun OwoUIDrawContext.drawScrollingText(
    textRenderer: TextRenderer,
    text: Text,
    centerX: Int,
    startX: Int,
    startY: Int,
    endX: Int,
    endY: Int,
    color: Int,
    textShadow: Boolean
) {
    val textWidth = textRenderer.getWidth(text)
    val j = (startY + endY - 9) / 2 + 1
    val buttonWidth = endX - startX
    if (textWidth <= buttonWidth)
        drawText(textRenderer, text, centerX - textWidth / 2, j, color, textShadow)
    else {
        val l = textWidth - buttonWidth
        val d = Util.getMeasuringTimeMs().toDouble() / 1000.0
        val e = max(l.toDouble() * 0.5, 3.0)
        val f = sin((Math.PI / 2) * cos((Math.PI * 2) * d / e)) / 2.0 + 0.5
        val g = MathHelper.lerp(f, 0.0, l.toDouble())
        enableScissor(startX, startY, endX, endY)
        drawText(textRenderer, text, startX - g.toInt(), j, color, textShadow)
        disableScissor()
    }
}