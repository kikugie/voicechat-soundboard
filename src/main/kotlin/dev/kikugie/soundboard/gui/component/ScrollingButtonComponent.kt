package dev.kikugie.soundboard.gui.component

import dev.kikugie.soundboard.util.drawScrollingText
import io.wispforest.owo.mixin.ui.access.ClickableWidgetAccessor
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.core.OwoUIDrawContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
import net.minecraft.text.Text
import java.util.function.Consumer

class ScrollingButtonComponent(message: Text, onPress: Consumer<ButtonComponent>?) : ButtonComponent(message, onPress) {
    private val margins get() = margins().get()

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderer.draw(context as OwoUIDrawContext, this, delta)

        val textRenderer = MinecraftClient.getInstance().textRenderer
        val color = if (this.active) 0xFFFFFF else 0xA0A0A0

        context.drawScrollingText(
            textRenderer,
            message,
            x + width / 2,
            x + margins.left,
            y + margins.top,
            x + width - margins.right,
            y + height - margins.bottom,
            color,
            textShadow
        )
        val tooltip = (this as ClickableWidgetAccessor).`owo$getTooltip`()
        if (this.hovered && tooltip.tooltip != null) context.drawTooltip(
            textRenderer, tooltip.tooltip!!
                .getLines(MinecraftClient.getInstance()), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY
        )
    }
}