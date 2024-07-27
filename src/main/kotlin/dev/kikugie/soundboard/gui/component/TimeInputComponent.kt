package dev.kikugie.soundboard.gui.component

import dev.kikugie.kowoui.changed
import dev.kikugie.soundboard.access.ValidatableField
import dev.kikugie.soundboard.util.Property
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.core.Sizing
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Suppress("LeakingThis")
abstract class TimeInputComponent(
    private val full: Duration, private val time: Property<Duration>,
) : TextBoxComponent(Sizing.fixed(full.printLength + 4)), ValidatableField {
    init {
        setMaxLength(full.toString().length.coerceAtLeast(1) + 3)
        changed { s ->
            s.asDuration?.takeIf(::isValid)?.let {
                val clamped = it.coerceAtMost(full)
                time.set(clamped)
                onChanged(clamped)
            }
        }
    }

    abstract fun isValid(duration: Duration): Boolean
    open fun onChanged(duration: Duration) {}

    override fun `soundboard$isValid`(text: String): Boolean =
        text.asDuration?.let {
            !it.isNegative() && (it - full <= 2.milliseconds) && isValid(it)
        } ?: false

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (!isFocused) text = time().asString
        super.renderWidget(context, mouseX, mouseY, delta)
    }

    companion object {
        val Duration.printLength: Int
            get() {
                val renderer = MinecraftClient.getInstance().textRenderer
                val length = inWholeSeconds.toString().length.coerceAtLeast(1)
                return renderer.getWidth("${"0".repeat(length)}.000_")
            }

        val Duration.asString get() = "%.2f".format(inWholeMilliseconds / 1000F)
        val String.asDuration get() = toDoubleOrNull()?.seconds
    }
}