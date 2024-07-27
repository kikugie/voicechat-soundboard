package dev.kikugie.soundboard.util

import com.mojang.blaze3d.systems.RenderSystem
import io.wispforest.owo.ui.core.Color
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.OwoUIDrawContext
import io.wispforest.owo.ui.core.ParentComponent
import io.wispforest.owo.ui.inject.GreedyInputComponent
import io.wispforest.owo.ui.parsing.UIModel
import net.minecraft.client.render.*
import org.joml.Vector2d

fun <T : ParentComponent> T.all(): Sequence<Component> = sequence {
    for (it in children()) {
        if (it is GreedyInputComponent) continue
        if (it is ParentComponent) for (it1 in it.all())
            yield(it1)
        yield(it)
    }
    yield(this@all)
}

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