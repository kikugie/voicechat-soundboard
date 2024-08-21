package dev.kikugie.soundboard.gui.widget

import dev.kikugie.kowoui.*
import dev.kikugie.kowoui.access.*
import dev.kikugie.kowoui.dynamic.dynamicButton
import dev.kikugie.kowoui.dynamic.dynamicLabel
import dev.kikugie.kowoui.experimental.at
import dev.kikugie.kowoui.experimental.plusAssign
import dev.kikugie.soundboard.CONFIG
import dev.kikugie.soundboard.ModKeyBinds
import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.audio.AudioConfiguration
import dev.kikugie.soundboard.audio.SoundEntry
import dev.kikugie.soundboard.audio.SoundRegistry
import dev.kikugie.soundboard.config.AudioConfig
import dev.kikugie.soundboard.entrypoint.SoundboardEntrypoint
import dev.kikugie.soundboard.gui.screen.SoundBrowser
import dev.kikugie.soundboard.gui.component.*
import dev.kikugie.soundboard.gui.component.TimeInputComponent.Companion.asString
import dev.kikugie.soundboard.util.client
import dev.kikugie.soundboard.util.currentScreen
import dev.kikugie.soundboard.util.duration
import dev.kikugie.soundboard.util.read
import io.wispforest.owo.ui.component.ButtonComponent.Renderer
import io.wispforest.owo.ui.component.SlimSliderComponent.Axis.VERTICAL
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.WrappingParentComponent
import io.wispforest.owo.ui.core.*
import io.wispforest.owo.ui.core.Insets.bottom
import io.wispforest.owo.ui.core.Sizing.*
import net.minecraft.text.Text
import kotlin.time.Duration

class SoundSettingsWidget(
    private val entry: SoundEntry,
    private val access: SoundboardEntrypoint,
) : WrappingParentComponent<FlowLayout>(fill(), fill(), verticalFlow { sizing = fill() }) {
    companion object {
        private const val CLOSE = "×"
        private const val CLOSE_TOOLTIP = "soundboard.browser.tooltip.close"
        private const val PLAY_TOOLTIP = "soundboard.browser.tooltip.play"
        private val STAR_LABEL: (Boolean) -> Text = {
            if (it) "★".text() else "☆".text()
        }
        private val FAVOURITE_TOOLTIP: (Boolean) -> Text = {
            if (it) "soundboard.browser.tooltip.unfavourite".translation()
            else "soundboard.browser.tooltip.favourite".translation()
        }
        private val PLAY_LABEL: (Boolean) -> Text = {
            if (it) "■".text() else "▶".text()
        }
    }

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
        var favourite = entry.id in Soundboard.config.favourites
        var index = -1
        this += horizontalFlow {
            id = "info-bar"
            gap = 2
            padding = bottom(2)
            horizontalSizing = fill()
            horizontalAlignment = HorizontalAlignment.RIGHT
            verticalAlignment = VerticalAlignment.CENTER
            this += ScrollingLabelComponent(entry.title).apply {
                id = "title"
                lineHeight = 8
                horizontalSizing = expand()
                verticalSizing = fixed(8)
                center { x + (width - it) / 2 }
            }
            this += object : ValidatableTextComponent() {
                init {
                    id = "index"
                    text = "-1"
                    verticalSizing = fixed(8)
                    horizontalSizing = fixed(client.textRenderer.getWidth("00"))
                    drawBackground = false
                    onChange {
                        if (`soundboard$isValid`(it)) index = it.trim().toInt()
                    }
                }

                override fun `soundboard$isValid`(text: String?): Boolean =
                    text?.trim()?.toIntOrNull()?.takeIf { it in -1..<CONFIG.favourites.size } != null

                override fun setText(text: String) {
                    super.setText(text.trim().take(2).padStart(2))
                }

                override fun write(text: String) {
                    super.write(text)
                    setText(this.text)
                }
            }
            this += button(STAR_LABEL(favourite)) {
                id = "favourite"
                sizing = fixed(8)
                renderer = Renderer.flat(0, 0, 0)
                tooltipText = FAVOURITE_TOOLTIP(favourite)
                onPress {
                    favourite = !favourite
                    message = STAR_LABEL(favourite)
                    tooltipText = FAVOURITE_TOOLTIP(favourite)

                    if (!favourite) CONFIG.favourites -= entry.id
                    else if (index < 0) CONFIG.favourites += entry.id
                    else CONFIG.favourites[index] = entry.id

                    SoundRegistry.updateFavourites()
                    (currentScreen as? SoundBrowser)?.createFavourites()
                }
            }
            this += button(CLOSE.text()) {
                id = "close"
                sizing = fixed(8)
                renderer = Renderer.flat(0, 0, 0)
                tooltipText = CLOSE_TOOLTIP.translation()
                onPress { (currentScreen as? SoundBrowser)?.closeSettings() }
            }
        }
        this += FlexibleGridLayout(2, 2).apply {
            id = "settings"
            verticalSizing = expand()
            horizontalAlignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            val waveform = WaveformComponent(data, settings.volume)
            val cutter = DurationCutterComponent(duration, settings::start, settings::end)
            at(0, 0) += stack {
                id = "waveform-cutter"
                sizing = expand()
                surface = Surface.PANEL_INSET
                padding = Insets.of(1)
                children(waveform, cutter)
            }
            at(0, 1) += slimSlider(VERTICAL) {
                id = "volume"
                verticalSizing = expand()
                value = settings.volume.coerceIn(0.0, 1.0).invert
                onChange {
                    val mod = it.invert
                    settings.volume = mod
                    waveform.mult = mod
                }
                tooltipSupplier {
                    "${(settings.volume * 100).toInt()}%".text()
                }
            }
            at(1, 0) += grid(1, 3) {
                id = "duration-controls"
                horizontalSizing = expand()
                horizontalAlignment = HorizontalAlignment.CENTER
                verticalAlignment = VerticalAlignment.CENTER
                at(0, 0) += TimeInputComponent(duration, settings::start).apply {
                    id = "start"
                    validate { it <= settings.end }
                    onDurationChange { cutter.update() }
                }
                at(0, 1) += dynamicLabel{
                    id = "duration"
                    horizontalTextAlignment = HorizontalAlignment.CENTER
                    verticalTextAlignment = VerticalAlignment.CENTER
                    text { "${(settings.end - settings.start).asString}s".text() }
                }
                at(0, 2) += TimeInputComponent(duration, settings::end).apply {
                    id = "end"
                    validate { it >= settings.start }
                    onDurationChange { cutter.update() }
                }
            }
            at(1, 1) += dynamicButton {
                id = "play"
                horizontalSizing = fixed(20)
                tooltipText = PLAY_TOOLTIP.translation(ModKeyBinds["browser"]!!.boundKeyLocalizedText.string)
                text { PLAY_LABEL(access.scheduler.playing) }
                onPress {
                    if (access.scheduler.playing) access.scheduler.reset()
                    else {
                        AudioConfig.save()
                        access.scheduleArray(data, true, settings)
                    }
                }
            }
        }
    }
}
