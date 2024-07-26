package dev.kikugie.soundboard.gui.widget

import dev.kikugie.soundboard.SoundRegistry
import dev.kikugie.soundboard.audio.AudioConfiguration
import dev.kikugie.soundboard.config.AudioConfig
import dev.kikugie.soundboard.entrypoint.SoundboardEntrypoint
import dev.kikugie.soundboard.gui.component.*
import dev.kikugie.soundboard.gui.component.TimeInputComponent.Companion.asString
import dev.kikugie.soundboard.util.*
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.component.SlimSliderComponent
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.GridLayout
import io.wispforest.owo.ui.container.WrappingParentComponent
import io.wispforest.owo.ui.core.*
import kotlin.time.Duration

class SoundSettingsWidget(
    private val entry: SoundRegistry.SoundEntry,
    access: SoundboardEntrypoint,
    horizontalSizing: Sizing,
    verticalSizing: Sizing,
) : WrappingParentComponent<GridLayout>(
    horizontalSizing,
    verticalSizing,
    FlexibleGridLayout(2, 2)
) {
    private val data = entry.read(access.format)
    private val duration = access.format.duration(data.size)
    private val default = AudioConfiguration(Duration.ZERO, duration, 1.0)
    private val settings = AudioConfig[entry] ?: default.clone()

    init {
        val waveform = WaveformComponent(data)
        val cutter = DurationCutterComponent(duration, settings::start, settings::end)
        // Top left
        val stack = Containers.stack(Sizing.expand(), Sizing.expand()).apply {
            padding(Insets.of(1))
            surface(Surface.PANEL_INSET)
            children(waveform, cutter)
        }

        // Top right
        val volume = Components.slimSlider(SlimSliderComponent.Axis.VERTICAL).apply {
            value(1 - settings.volume)
            verticalSizing(Sizing.expand())
            ended { settings.volume = 1 - value() }
        }

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
        // Bottom right
        val times = Containers.grid(Sizing.expand(), Sizing.content(), 1, 3)
            .child(min,0, 0)
            .child(label, 0, 1)
            .child(max, 0, 2)
            .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
        // Bottom left
        val play = Components.button("▶".asText()) {
            access.scheduleArray(data, true, settings)
        }.horizontalSizing(Sizing.fixed(20))

        with(child) {
            horizontalAlignment(HorizontalAlignment.CENTER)
            verticalAlignment(VerticalAlignment.CENTER)
            padding(Insets.of(5))
            child(stack, 0, 0)
            child(volume, 0, 1)
            child(times, 1, 0)
            child(play, 1, 1)
        }
    }

    fun update() {
        if (settings != default) AudioConfig[entry] = settings
    }

    override fun draw(context: OwoUIDrawContext, mouseX: Int, mouseY: Int, partialTicks: Float, delta: Float) {
        super.draw(context, mouseX, mouseY, partialTicks, delta)
        child.draw(context, mouseX, mouseY, partialTicks, delta)
    }
}
