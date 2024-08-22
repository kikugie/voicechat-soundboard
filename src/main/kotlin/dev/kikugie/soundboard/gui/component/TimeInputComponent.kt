package dev.kikugie.soundboard.gui.component

import dev.kikugie.kowoui.dynamic.ColoredTextComponent
import dev.kikugie.kowoui.onChange
import dev.kikugie.soundboard.util.Property
import io.wispforest.owo.ui.core.Sizing
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class TimeInputComponent(
    private val full: Duration, private val time: Property<Duration>,
) : ColoredTextComponent() {
    private lateinit var validator: (Duration) -> Boolean
    private var listener: (Duration) -> Unit = {}

    init {
        horizontalSizing(Sizing.fixed(full.printLength + 4))
        setMaxLength(full.toString().length.coerceAtLeast(1) + 3)
        setTextPredicate {
            text.asDuration?.let { !it.isNegative() && (it - full <= 2.milliseconds) && validator(it) } ?: false
        }
        onChange { s ->
            s.asDuration?.takeIf(validator::invoke)?.let {
                val clamped = it.coerceAtMost(full)
                time.set(clamped)
                listener(clamped)
            }
        }
    }

    fun validate(action: (Duration) -> Boolean) {
        validator = action
    }

    fun onDurationChange(action: (Duration) -> Unit) {
        listener = action
    }

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