package dev.kikugie.soundboard.gui.component

import io.wispforest.owo.mixin.ui.access.ClickableWidgetAccessor
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.core.OwoUIDrawContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
import net.minecraft.text.Text
import net.minecraft.util.Util
import net.minecraft.util.math.MathHelper
import java.util.function.Consumer
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class ScrollingButtonComponent(message: Text, onPress: Consumer<ButtonComponent>?) : ButtonComponent(message, onPress) {
    private val margins get() = margins().get()

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderer.draw(context as OwoUIDrawContext, this, delta)

        val textRenderer = MinecraftClient.getInstance().textRenderer
        val color = if (this.active) 0xFFFFFF else 0xA0A0A0

        renderScrollableText(context, textRenderer, message, color = color)
        val tooltip = (this as ClickableWidgetAccessor).`owo$getTooltip`()
        if (this.hovered && tooltip.tooltip != null) context.drawTooltip(
            textRenderer, tooltip.tooltip!!
                .getLines(MinecraftClient.getInstance()), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY
        )
    }

    private fun renderScrollableText(
        context: DrawContext,
        textRenderer: TextRenderer,
        text: Text,
        centerX: Int = x + width / 2,
        startX: Int = x + margins.left,
        startY: Int = y + margins.top,
        endX: Int = x + width - margins.right,
        endY: Int = y + height - margins.bottom,
        color: Int,
    ) {
        val textWidth = textRenderer.getWidth(text)
        val j = (startY + endY - 9) / 2 + 1
        val buttonWidth = endX - startX
        if (textWidth <= buttonWidth)
            context.drawText(textRenderer, text, centerX - textWidth / 2, j, color, textShadow)
        else {
            val l = textWidth - buttonWidth
            val d = Util.getMeasuringTimeMs().toDouble() / 1000.0
            val e = max(l.toDouble() * 0.5, 3.0)
            val f = sin((Math.PI / 2) * cos((Math.PI * 2) * d / e)) / 2.0 + 0.5
            val g = MathHelper.lerp(f, 0.0, l.toDouble())
            context.enableScissor(startX, startY, endX, endY)
            context.drawText(textRenderer, text, startX - g.toInt(), j, color, textShadow)
            context.disableScissor()
        }
    }
}