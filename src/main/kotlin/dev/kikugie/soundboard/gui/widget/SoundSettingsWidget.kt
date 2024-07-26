package dev.kikugie.soundboard.gui.widget

import dev.kikugie.soundboard.SoundRegistry
import dev.kikugie.soundboard.audio.AudioConfiguration
import dev.kikugie.soundboard.config.AudioConfig
import dev.kikugie.soundboard.entrypoint.SoundboardEntrypoint
import dev.kikugie.soundboard.gui.component.DurationCutterComponent
import dev.kikugie.soundboard.gui.component.DynamicTextComponent
import dev.kikugie.soundboard.gui.component.TimeInputComponent
import dev.kikugie.soundboard.gui.component.TimeInputComponent.Companion.asString
import dev.kikugie.soundboard.gui.component.WaveformComponent
import dev.kikugie.soundboard.util.children
import dev.kikugie.soundboard.util.duration
import dev.kikugie.soundboard.util.read
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.WrappingParentComponent
import io.wispforest.owo.ui.core.*
import kotlin.time.Duration

class SoundSettingsWidget(
    private val entry: SoundRegistry.SoundEntry,
    access: SoundboardEntrypoint,
    horizontalSizing: Sizing,
    verticalSizing: Sizing,
) : WrappingParentComponent<FlowLayout>(
    horizontalSizing,
    verticalSizing,
    Containers.verticalFlow(Sizing.fill(), Sizing.fill())
) {
    private val data = entry.read(access.format)
    private val duration = access.format.duration(data.size)
    private val default = AudioConfiguration(Duration.ZERO, duration, 1F)
    private val settings = AudioConfig[entry] ?: default.clone()

    init {
        val waveform = WaveformComponent(data, Sizing.fill(), Sizing.expand())
        val cutter = DurationCutterComponent(duration, settings::start, settings::end)

        val stack = Containers.stack(Sizing.fill(), Sizing.expand())
            .children(waveform, cutter)
        val label = object : DynamicTextComponent() {
            init {
                horizontalTextAlignment(HorizontalAlignment.CENTER)
                verticalTextAlignment(VerticalAlignment.CENTER)
            }

            override val string: String get() = "${(settings.end - settings.start).asString}s"
        }
        val min = object : TimeInputComponent(duration, settings::start) {
            override fun isValid(duration: Duration): Boolean = duration <= settings.end
            override fun onChanged(duration: Duration) = cutter.update()
        }
        val max = object : TimeInputComponent(duration, settings::end) {
            override fun isValid(duration: Duration): Boolean = duration >= settings.start
            override fun onChanged(duration: Duration) = cutter.update()
        }
        val grid = Containers.grid(Sizing.fill(), Sizing.content(2), 1, 3)
            .child(min,0, 0)
            .child(label, 0, 1)
            .child(max, 0, 2)
            .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
        child.gap(2)
        child.padding(Insets.of(3))
        child.children(stack, grid)
    }

    fun update() {
        if (settings != default) AudioConfig[entry] = settings
    }

    override fun draw(context: OwoUIDrawContext, mouseX: Int, mouseY: Int, partialTicks: Float, delta: Float) {
        super.draw(context, mouseX, mouseY, partialTicks, delta)
        child.draw(context, mouseX, mouseY, partialTicks, delta)
    }
}