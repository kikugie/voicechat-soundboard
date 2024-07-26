package dev.kikugie.soundboard.gui

import dev.kikugie.soundboard.SoundRegistry
import dev.kikugie.soundboard.audio.AudioConfiguration
import dev.kikugie.soundboard.audio.bytesToShorts
import dev.kikugie.soundboard.audio.convert
import dev.kikugie.soundboard.entrypoint.SoundboardAccess
import dev.kikugie.soundboard.gui.component.DurationCutterComponent
import dev.kikugie.soundboard.gui.component.ScrollingButtonComponent
import dev.kikugie.soundboard.gui.component.WaveformComponent
import dev.kikugie.soundboard.gui.widget.SoundSettingsWidget
import dev.kikugie.soundboard.mixin.owo_ui.GridLayoutAccessor
import dev.kikugie.soundboard.mixin.owo_ui.ScrollContainerAccessor
import dev.kikugie.soundboard.util.*
import io.wispforest.owo.ui.base.BaseUIModelScreen
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.container.*
import io.wispforest.owo.ui.core.*
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.option.KeyBinding
import net.minecraft.text.Text
import net.minecraft.util.Util
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.ceil
import kotlin.time.Duration.Companion.seconds

class SoundBrowser : BaseUIModelScreen<FlowLayout>(FlowLayout::class.java, BROWSER) {
    private var scrollbar: ScrollContainerAccessor? = null

    override fun build(root: FlowLayout) {
        SoundRegistry.update()
        populateEntries(root)
        scrollbar = root.childById<ScrollContainer<*>>("scroll") as? ScrollContainerAccessor
        for (it in root.all()) it.keyPress { key, scan, _ ->
            val found = keybinds
                .firstOrNull { it.first.matchesKey(key, scan) }
                ?.also { it.second(this) }
            found != null
        }
    }

    override fun init() {
        super.init()
        scrollbar?.invokeScrollBy(savedOffset, true, false)
    }

    override fun close() {
        savedOffset = scrollbar?.scrollOffset ?: 0.0
        super.close()
    }

    override fun shouldPause(): Boolean = false

    private fun populateEntries(root: FlowLayout) {
        val container: FlowLayout = root.childById("container")!!
        container.children(SoundRegistry.entries.mapNotNull{create(root, it)}.toList())
    }

    private fun create(root: FlowLayout, group: SoundRegistry.SoundGroup): FlowLayout? {
        if (group.entries.isEmpty()) return null
        val buttons = group.entries.map { entry ->
            button(entry.title(group)) {
                if (Screen.hasControlDown()) createWaveformOverlay(root, entry)
                else SoundboardAccess.play(Screen.hasShiftDown(), entry)
            }
        }
        val path = group.path
        val template = group()
        val label: CollapsibleContainer = template.childById("collapse") ?: return null
        val location = runCatching { SoundRegistry.BASE_DIR.resolve(path).toFile() }.getOrNull()
        val locationExists = location?.exists() == true
        if (locationExists) label.mouseDown { _, _, _ ->
            Screen.hasShiftDown().also { if (it) Util.getOperatingSystem().open(location) }
        }
        val child = label.titleLayout().children().firstOrNull { it is LabelComponent } as? LabelComponent
        if (locationExists) child?.tooltip(DIRECTORY_TOOLTIP.asTranslation())
        child?.text(group.title())

        val contents: GridLayout = label.childById("contents") ?: return null
        contents as GridLayoutAccessor
        val columns = contents.columns
        val rows = ceil(buttons.size / columns.toDouble()).toInt()
        contents.rows = rows
        contents.children = arrayOfNulls(rows * columns)
        buttons.forEachIndexed { i, it ->
            contents.child(it as Component, i / columns, i % columns)
        }

        if (label.expanded() && path in collapsedPaths)
            label.toggleExpansion()
        label.toggled {
            if (it) collapsedPaths.remove(path) else collapsedPaths.add(path)
        }
        return template
    }

    private fun createWaveformOverlay(root: FlowLayout, entry: SoundRegistry.SoundEntry) {
        val format = SoundboardAccess.first { format } ?: return
        val window = MinecraftClient.getInstance().window
//        val data = bytesToShorts(
//            entry.supplier().use {
//                convert(AudioSystem.getAudioInputStream(BufferedInputStream(it)), format).readAllBytes()
//            }
//        )
//        val waveform = WaveformComponent(data)
//        val obj = object {
//            var min = 1.seconds
//            var max = 9.seconds
//        }
//
//        val cutter = DurationCutterComponent(
//            10.seconds,
//            obj::min,
//            obj::max,
//        )
//        val stack = Containers.stack(Sizing.fill(), Sizing.fill())
//            .child(waveform)
//            .child(cutter)
//            .padding(Insets.of(3))
        val widget = SoundSettingsWidget(
            entry,
            SoundboardAccess.delegates.first(), null,
            Sizing.fill(), Sizing.fill()
        )


        val overlay = Containers.overlay(widget)
            .closeOnClick(false)
            .surface(Surface.PANEL)
            .positioning(Positioning.absolute(window.scaledWidth / 2 - 100, window.scaledHeight / 2 - 100))
            .sizing(Sizing.fill(50), Sizing.fill(50))
            .zIndex(100)
            .mouseDown { _, _, _ -> true }

        root.child(overlay)
    }

    private fun group(): FlowLayout = model.template("group")
    private fun button(name: Text, onPress: (ButtonComponent) -> Unit): ButtonComponent =
        ScrollingButtonComponent(name, onPress).apply {
            val muted = SoundboardAccess.all { muted }
            active(!muted)

            val temp: ButtonComponent = model.template("button")
            renderer(temp.renderer())
            textShadow(temp.textShadow())
            cursorStyle(temp.cursorStyle())
            positioning(temp.positioning().get())
            margins(temp.margins().get())
            horizontalSizing(temp.horizontalSizing().get())
            verticalSizing(temp.verticalSizing().get())
            tooltip(temp.tooltip())
            zIndex(temp.zIndex())
        }

    companion object : ScreenManager(SoundBrowser::class) {
        val BROWSER = idOf("browser")

        private const val FILE_TOOLTIP = "soundboard.browser.tooltip.file"
        private const val DIRECTORY_TOOLTIP = "soundboard.browser.tooltip.directory"
        private const val MUTED_TOOLTIP = "soundboard.browser.tooltip.unavailable"

        private var savedOffset = 0.0
        private val collapsedPaths = mutableSetOf<String>()
        private val keybinds = mutableListOf<Pair<KeyBinding, (SoundBrowser) -> Unit>>()

        fun keyAction(key: KeyBinding, action: SoundBrowser.() -> Unit) {
            keybinds += key to action
        }
    }
}