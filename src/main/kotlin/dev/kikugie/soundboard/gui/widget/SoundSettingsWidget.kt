package dev.kikugie.soundboard.gui.widget

import dev.kikugie.kowoui.*
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
import io.wispforest.owo.ui.component.ButtonComponent
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

    private fun create() = with(child) {
        padding = Insets.of(5)
        horizontalFlow {
            this.padding = Insets.bottom(2)
            horizontalSizing = Sizing.fill()
            horizontalAlignment = HorizontalAlignment.RIGHT
            gap = 2
            child(ScrollingLabelComponent(entry.title).apply {
                horizontalSizing = Sizing.expand()
                verticalSizing = Sizing.fixed(8)
                lineHeight = 8
            })
            child(object : DynamicButtonComponent() {
                private var favourite = entry.id in Soundboard.config.favourites
                private val tooltip get() = if (favourite) "soundboard.browser.tooltip.unfavourite"
                else "soundboard.browser.tooltip.favourite"
                override val string: String get() = if (favourite) "★" else "☆"

                init {
                    sizing = Sizing.fixed(8)
                    renderer = Renderer.flat(0, 0, 0)
                    tooltipText = tooltip.translation()
                    onPress { toggleFavourite() }
                }

                private fun toggleFavourite() {
                    favourite = !favourite
                    tooltipText = tooltip.translation()
                    if (favourite) Soundboard.config.favourites += entry.id
                    else Soundboard.config.favourites -= entry.id
                    SoundRegistry.updateFavourites()
                    (currentScreen as? SoundBrowser)?.createFavourites()
                }
            })
            button("×".text()) {
                sizing = Sizing.fixed(8)
                renderer = ButtonComponent.Renderer.flat(0, 0, 0)
                tooltipText = "soundboard.browser.tooltip.close".translation()
                onPress { (currentScreen as? SoundBrowser)?.closeSettings() }
            }
        }
        child(FlexibleGridLayout(2, 2).apply {
            verticalSizing = Sizing.expand()
            horizontalAlignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
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
            setChild(1, 1, object : DynamicButtonComponent() {
                private val playing get() = access.scheduler.playing
                override val string: String get() = if (playing) "■" else "▶"

                init {
                    val key = Soundboard.keybinds["browser"]!!.boundKeyLocalizedText.string
                    horizontalSizing(Sizing.fixed(20))
                    tooltipText = "soundboard.browser.tooltip.play".translation(key)
                    onPress {
                        if (playing) access.scheduler.reset()
                        else access.scheduleArray(data, true, settings)
                    }
                }
            })
        })
    }
}
