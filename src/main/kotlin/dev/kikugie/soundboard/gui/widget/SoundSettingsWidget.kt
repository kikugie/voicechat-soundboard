package dev.kikugie.soundboard.gui.widget

import dev.kikugie.kowoui.*
import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.audio.AudioConfiguration
import dev.kikugie.soundboard.audio.SoundEntry
import dev.kikugie.soundboard.config.AudioConfig
import dev.kikugie.soundboard.entrypoint.SoundboardEntrypoint
import dev.kikugie.soundboard.gui.component.*
import dev.kikugie.soundboard.gui.component.TimeInputComponent.Companion.asString
import dev.kikugie.soundboard.util.duration
import dev.kikugie.soundboard.util.read
import io.wispforest.owo.ui.component.SlimSliderComponent.Axis.VERTICAL
import io.wispforest.owo.ui.container.GridLayout
import io.wispforest.owo.ui.container.WrappingParentComponent
import io.wispforest.owo.ui.core.*
import kotlin.time.Duration

class SoundSettingsWidget(
    private val entry: SoundEntry,
    access: SoundboardEntrypoint,
) : WrappingParentComponent<GridLayout>(Sizing.fill(), Sizing.fill(), FlexibleGridLayout(2, 2)) {
    private val data = entry.read(access.format)
    private val duration = access.format.duration(data.size)
    private val default = AudioConfiguration(Duration.ZERO, duration, 1.0)
    private val settings = AudioConfig[entry] ?: default.clone()

    init {
        with(child) {
            horizontalAlignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            padding = Insets.of(5)
            val cutter = DurationCutterComponent(duration, settings::start, settings::end)
            stack(0, 0) {
                sizing = Sizing.expand()
                surface = Surface.PANEL_INSET
                padding = Insets.of(1)
                children(WaveformComponent(data), cutter)
            }
            slimSlider(0, 1, VERTICAL) {
                verticalSizing = Sizing.expand()
                value = 1 - settings.volume
                slideEnded { settings.volume = 1 - value }
                tooltipSupplier { "${((1 - it) * 100).toInt()}%".text() }
            }
            grid(1, 0, 1, 3) {
                horizontalSizing = Sizing.expand()
                horizontalAlignment = HorizontalAlignment.CENTER
                verticalAlignment = VerticalAlignment.CENTER
                setChild(0, 0, object : TimeInputComponent(duration, settings::start) {
                    override fun isValid(duration: Duration): Boolean = duration <= settings.end
                    override fun onChanged(duration: Duration) = cutter.update()
                })
                setChild(0, 1, object : DynamicTextComponent() {
                    init {
                        horizontalTextAlignment = HorizontalAlignment.CENTER
                        verticalTextAlignment = VerticalAlignment.CENTER
                    }

                    override val string: String get() = "${(settings.end - settings.start).asString}s"
                })
                setChild(0, 2, object : TimeInputComponent(duration, settings::end) {
                    override fun isValid(duration: Duration): Boolean = duration >= settings.start
                    override fun onChanged(duration: Duration) = cutter.update()
                })
            }
            button(1, 1, "▶".text()) {
                val key = Soundboard.keybinds["browser"]!!.boundKeyLocalizedText.string
                horizontalSizing = Sizing.fixed(20)
                tooltipText = "soundboard.browser.tooltip.play".translation(key)
                onPress { access.scheduleArray(data, true, settings) }
            }
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
