package dev.kikugie.soundboard.gui.widget

import dev.kikugie.kowoui.*
import dev.kikugie.kowoui.access.*
import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.audio.AudioConfiguration
import dev.kikugie.soundboard.audio.SoundEntry
import dev.kikugie.soundboard.audio.SoundRegistry
import dev.kikugie.soundboard.config.AudioConfig
import dev.kikugie.soundboard.entrypoint.SoundboardEntrypoint
import dev.kikugie.soundboard.gui.SoundBrowser
import dev.kikugie.soundboard.gui.component.*
import dev.kikugie.soundboard.gui.component.TimeInputComponent.Companion.asString
import dev.kikugie.soundboard.util.currentScreen
import dev.kikugie.soundboard.util.duration
import dev.kikugie.soundboard.util.read
import io.wispforest.owo.ui.component.ButtonComponent.Renderer
import io.wispforest.owo.ui.component.SlimSliderComponent.Axis.VERTICAL
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.WrappingParentComponent
import io.wispforest.owo.ui.core.*
import kotlin.time.Duration

class SoundSettingsWidget(
    private val entry: SoundEntry,
    private val access: SoundboardEntrypoint,
) : WrappingParentComponent<FlowLayout>(Sizing.fill(), Sizing.fill(), verticalFlow { sizing = Sizing.fill() }) {
    private val data = entry.read(access.format)
    private val duration = access.format.duration(data.size)
    private val default = AudioConfiguration(Duration.ZERO, duration, 1.0)
    private val settings = AudioConfig[entry] ?: default.clone()

    init {
        create()
    }

    fun update() {
        if (settings != default) AudioConfig[entry] = settings
    }

    override fun draw(context: OwoUIDrawContext, mouseX: Int, mouseY: Int, partialTicks: Float, delta: Float) {
        super.draw(context, mouseX, mouseY, partialTicks, delta)
        child.draw(context, mouseX, mouseY, partialTicks, delta)
    }

    private val Double.invert get() = 1 - this

    private fun create() = with(child) {
        padding = Insets.of(5)
        at() set horizontalFlow {
            // Top row
            padding = Insets.bottom(2)
            horizontalSizing = Sizing.fill()
            horizontalAlignment = HorizontalAlignment.RIGHT
            gap = 2
            // Sound name
            at() set ScrollingLabelComponent().apply {
                center { x + (width - it) / 2 }
                text = entry.title
                horizontalSizing = Sizing.expand()
                verticalSizing = Sizing.fixed(8)
                lineHeight = 8
            }
            at() set DynamicButtonComponent().apply {
                var favourite = entry.id in Soundboard.config.favourites
                fun key() = if (favourite) "soundboard.browser.tooltip.unfavourite"
                else "soundboard.browser.tooltip.favourite"

                sizing = Sizing.fixed(8)
                renderer = Renderer.flat(0, 0, 0)
                string = if (favourite) "★" else "☆"
                tooltipText = key().translation()
                onPress {
                    favourite = !favourite
                    string = if (favourite) "★" else "☆"
                    tooltipText = key().translation()

                    if (favourite) Soundboard.config.favourites += entry.id
                    else Soundboard.config.favourites -= entry.id
                    SoundRegistry.updateFavourites()
                    (currentScreen as? SoundBrowser)?.createFavourites()
                }
            }
            at() set button("×".text()) {
                sizing = Sizing.fixed(8)
                renderer = Renderer.flat(0, 0, 0)
                tooltipText = "soundboard.browser.tooltip.close".translation()
                onPress { (currentScreen as? SoundBrowser)?.closeSettings() }
            }
        }
        at() set FlexibleGridLayout(2, 2).apply {
            verticalSizing = Sizing.expand()
            horizontalAlignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            val waveform = WaveformComponent(data, settings.volume)
            val cutter = DurationCutterComponent(duration, settings::start, settings::end)
            at(0, 0) set stack {
                sizing = Sizing.expand()
                surface = Surface.PANEL_INSET
                padding = Insets.of(1)
                children(waveform, cutter)
            }
            at(0, 1) set slimSlider(VERTICAL) {
                verticalSizing = Sizing.expand()
                value = settings.volume.coerceIn(0.0, 1.0).invert
                onChange {
                    val mod = it.invert
                    settings.volume = mod
                    waveform.mult = mod
                }
                tooltipSupplier { "${(settings.volume * 100).toInt()}%".text() }
            }
            at(1, 0) set grid(1, 3) {
                horizontalSizing = Sizing.expand()
                horizontalAlignment = HorizontalAlignment.CENTER
                verticalAlignment = VerticalAlignment.CENTER
                at(0, 0) set TimeInputComponent(duration, settings::start).apply {
                    validate { it <= settings.end }
                    onDurationChange { cutter.update() }
                }
                at(0, 1) set DynamicTextComponent().apply {
                    horizontalTextAlignment = HorizontalAlignment.CENTER
                    verticalTextAlignment = VerticalAlignment.CENTER
                    string { "${(settings.end - settings.start).asString}s" }
                }
                at(0, 2) set TimeInputComponent(duration, settings::end).apply {
                    validate { it >= settings.start }
                    onDurationChange { cutter.update() }
                }
            }
            at(1, 1) set DynamicButtonComponent().apply {
                val key = Soundboard.keybinds["browser"]!!.boundKeyLocalizedText.string
                string { if (access.scheduler.playing) "■" else "▶" }
                tooltipText = "soundboard.browser.tooltip.play".translation(key)
                horizontalSizing = Sizing.fixed(20)
                onPress {
                    if (access.scheduler.playing) {
                        access.scheduler.reset()
                    } else {
                        AudioConfig.save()
                        access.scheduleArray(data, true, settings)
                    }
                }
            }
        }
    }
}
