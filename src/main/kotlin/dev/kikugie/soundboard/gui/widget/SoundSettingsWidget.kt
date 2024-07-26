package dev.kikugie.soundboard.gui.widget

import dev.kikugie.soundboard.SoundRegistry
import dev.kikugie.soundboard.audio.AudioConfiguration
import dev.kikugie.soundboard.audio.bytesToShorts
import dev.kikugie.soundboard.audio.convert
import dev.kikugie.soundboard.entrypoint.SoundboardEntrypoint
import dev.kikugie.soundboard.gui.component.DurationCutterComponent
import dev.kikugie.soundboard.gui.component.TimeInputComponent
import dev.kikugie.soundboard.gui.component.TimeInputComponent.Companion.asString
import dev.kikugie.soundboard.gui.component.WaveformComponent
import dev.kikugie.soundboard.util.asText
import dev.kikugie.soundboard.util.children
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.WrappingParentComponent
import io.wispforest.owo.ui.core.*
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SoundSettingsWidget(
    private val entry: SoundRegistry.SoundEntry,
    private val access: SoundboardEntrypoint,
    configuration: AudioConfiguration? = null,
    horizontalSizing: Sizing,
    verticalSizing: Sizing,
) : WrappingParentComponent<FlowLayout>(
    horizontalSizing,
    verticalSizing,
    Containers.verticalFlow(Sizing.fill(), Sizing.fill())
) {
    private val data = bytesToShorts(
        entry.supplier().use {
            convert(AudioSystem.getAudioInputStream(BufferedInputStream(it)), access.format).readAllBytes()
        }
    )
    private val duration = (data.size / (access.frameSize * 50.0)).seconds
    private val settings = configuration ?: AudioConfiguration(Duration.ZERO, duration, 1F)

    init {
        val waveform = WaveformComponent(data, Sizing.fill(), Sizing.expand())
        val cutter = DurationCutterComponent(duration, settings::start, settings::end)
        val stack = Containers.stack(Sizing.fill(), Sizing.expand())
            .children(waveform, cutter)
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
            .child(Components.label("-${duration.asString}-".asText()), 0, 1)
            .child(max, 0, 2)
            .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
        child.gap(3)
        child.padding(Insets.of(3))
        child.children(stack, grid)
    }

    override fun draw(context: OwoUIDrawContext, mouseX: Int, mouseY: Int, partialTicks: Float, delta: Float) {
        super.draw(context, mouseX, mouseY, partialTicks, delta)
        child.draw(context, mouseX, mouseY, partialTicks, delta)
    }
}