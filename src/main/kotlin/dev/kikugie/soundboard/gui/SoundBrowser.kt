package dev.kikugie.soundboard.gui

import dev.kikugie.kowoui.*
import dev.kikugie.soundboard.SoundRegistry
import dev.kikugie.soundboard.SoundRegistry.update
import dev.kikugie.soundboard.entrypoint.SoundboardAccess
import dev.kikugie.soundboard.gui.component.ScrollingButtonComponent
import dev.kikugie.soundboard.gui.widget.SoundSettingsWidget
import dev.kikugie.soundboard.mixin.owo_ui.GridLayoutAccessor
import dev.kikugie.soundboard.mixin.owo_ui.ScrollContainerAccessor
import dev.kikugie.soundboard.util.*
import io.wispforest.owo.ui.base.BaseUIModelScreen
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.container.*
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.Positioning
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.core.Surface
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.option.KeyBinding
import net.minecraft.text.Text
import net.minecraft.util.Util
import kotlin.math.ceil

class SoundBrowser : BaseUIModelScreen<FlowLayout>(FlowLayout::class.java, BROWSER) {
    private lateinit var root: FlowLayout
    private var scrollbar: ScrollContainerAccessor? = null
    internal var settings: SoundSettingsWidget? = null

    fun closeSettings() {
        settings?.update()
        settings?.parent()?.let { root.removeChild(it) }
        settings = null
    }

    override fun build(root: FlowLayout) {
        this.root = root
        update()
        populateEntries()
        scrollbar = root.childById<ScrollContainer<*>>("scroll") as? ScrollContainerAccessor
        for (it in root.all()) it.keyPress { key, scan, _ ->
            keybinds.firstOrNull { it.first.matchesKey(key, scan) }
                ?.also { it.second(this) } != null
        }
    }

    override fun init() {
        super.init()
        scrollbar?.invokeScrollBy(savedOffset, true, false)
    }

    override fun close() {
        savedOffset = scrollbar?.scrollOffset ?: 0.0
        settings?.update()
        super.close()
    }

    override fun shouldPause(): Boolean = false

    private fun populateEntries() {
        val container: FlowLayout = root.childById("container")!!
        container.children(SoundRegistry.entries.mapNotNull { create(it) }.toList())
    }

    private fun create(group: SoundRegistry.SoundGroup): FlowLayout? {
        if (group.entries.isEmpty()) return null
        val buttons = group.entries.map { entry ->
            button(entry.title()) {
                settings?.update()
                if (Screen.hasControlDown()) createWaveformOverlay(entry)
                else SoundboardAccess.play(entry, Screen.hasShiftDown())
            }
        }
        val path = group.path
        val template: FlowLayout = model.template("group")
        val label: CollapsibleContainer = template.childById("collapse") ?: return null
        val location = runCatching { SoundRegistry.BASE_DIR.resolve(path).toFile() }.getOrNull()
        val locationExists = location?.exists() == true
        if (locationExists) label.mouseDown { _, _, _ ->
            Screen.hasShiftDown().also { if (it) Util.getOperatingSystem().open(location) }
        }
        val child = label.titleLayout().children().firstOrNull { it is LabelComponent } as? LabelComponent
        if (locationExists) child?.tooltip(DIRECTORY_TOOLTIP.translation())
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

    private fun createWaveformOverlay(entry: SoundRegistry.SoundEntry) {
        settings = SoundSettingsWidget(entry, SoundboardAccess.delegates.first())
        root.overlay(settings!!) {
            sizing = Sizing.fill(50)
            closeOnClick = false
            surface = Surface.PANEL
            positioning = Positioning.relative(50, 50)
            zIndex = 100
            mouseDown { _, _, _ -> true }
            keyPress { key, scan, _ ->
                keybinds.firstOrNull { it.first.matchesKey(key, scan) }
                    ?.also { it.second(this@SoundBrowser) } != null
            }
        }
    }

    private fun button(name: Text, onPress: (ButtonComponent) -> Unit): ButtonComponent =
        ScrollingButtonComponent(name, onPress).apply {
            active = !SoundboardAccess.all { muted }

            val temp: ButtonComponent = model.template("button")
            renderer = temp.renderer
            textShadow = temp.textShadow
            cursorStyle = temp.cursorStyle
            positioning = temp.positioning
            margins = temp.margins
            horizontalSizing = temp.horizontalSizing
            verticalSizing = temp.verticalSizing
            tooltip = temp.tooltip
            zIndex = temp.zIndex
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