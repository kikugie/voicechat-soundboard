package dev.kikugie.soundboard.gui

import dev.kikugie.kowoui.*
import dev.kikugie.soundboard.audio.SoundEntry
import dev.kikugie.soundboard.audio.SoundGroup
import dev.kikugie.soundboard.audio.SoundRegistry
import dev.kikugie.soundboard.entrypoint.SoundboardAccess
import dev.kikugie.soundboard.entrypoint.SoundboardAccess.play
import dev.kikugie.soundboard.gui.component.ScrollingButtonComponent
import dev.kikugie.soundboard.gui.widget.SoundSettingsWidget
import dev.kikugie.soundboard.mixin.owo_ui.GridLayoutAccessor
import dev.kikugie.soundboard.mixin.owo_ui.ScrollContainerAccessor
import dev.kikugie.soundboard.util.*
import io.wispforest.owo.ui.base.BaseUIModelScreen
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.container.*
import io.wispforest.owo.ui.core.Positioning
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.core.Surface
import net.minecraft.client.option.KeyBinding
import net.minecraft.text.Text
import net.minecraft.util.Util
import kotlin.io.path.exists
import kotlin.math.ceil

class SoundBrowser : BaseUIModelScreen<FlowLayout>(FlowLayout::class.java, BROWSER) {
    private lateinit var root: FlowLayout
    private var favourites: FlowLayout? = null
    private var scrollbar: ScrollContainerAccessor? = null
    internal var settings: SoundSettingsWidget? = null

    fun closeSettings() {
        settings?.update()
        settings?.parent()?.let { root.removeChild(it) }
        settings = null
    }

    fun createFavourites(container: FlowLayout = root.childById<FlowLayout>("container")!!) {
        container.removeChild(favourites)
        favourites = group(SoundRegistry.favourites, false)
        if (favourites != null) container.child(0, favourites)
    }

    override fun build(root: FlowLayout) = with(root) {
        this@SoundBrowser.root = root
        SoundRegistry.update()
        childById<FlowLayout>("container")?.apply {
            createFavourites(this)
            children(SoundRegistry.groups.mapNotNull { group(it, it.path.isEmpty()) }.toList())
        } ?: error("Missing browser container")
        scrollbar = childById<ScrollContainer<*>>("scroll") as? ScrollContainerAccessor
        for (it in all()) it.keyPress { key, scan, _ ->
            keybinds.firstOrNull { it.first.matchesKey(key, scan) }
                ?.also { it.second(this@SoundBrowser) } != null
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

    private fun group(group: SoundGroup, keepEmpty: Boolean): FlowLayout? =
        if (group.entries.isEmpty() && !keepEmpty) null
        else model.template<FlowLayout>("group").apply {
            val path = runCatching { SoundRegistry.BASE_DIR.resolve(group.path) }.getOrNull()?.takeIf { it.exists() }
            val container = childById<CollapsibleContainer>("collapse")?.apply {
                if (path != null) mouseDown { _, _, _ -> shiftDown then { Util.getOperatingSystem().open(path) } }
                if (group.path in collapsedPaths) expanded = false
                toggled { if (it) collapsedPaths += group.path else collapsedPaths -= group.path }
            } ?: error("Missing group container")

            val buttons = group.entries.map {
                this@SoundBrowser.button(it.title) { _ ->
                    settings?.update()
                    if (ctrlDown) settings(it)
                    else play(it, shiftDown)
                }
            }

            container.titleLayout().children.filterIsInstance<LabelComponent>().firstOrNull()?.apply {
                text = group.title
                if (path != null) tooltipText = DIRECTORY_TOOLTIP.translation()
            } ?: error("Missing group header")

            container.childById<GridLayout>("contents")?.apply {
                this as GridLayoutAccessor
                rows = ceil(buttons.size / columns.toDouble()).toInt()
                children = arrayOfNulls(rows * columns)
                for ((i, button) in buttons.withIndex())
                    child(button, i / columns, i % columns)
            } ?: error("Missing group grid")
        }


    private fun settings(entry: SoundEntry) {
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

            active = !SoundboardAccess.all { muted }
        }

    companion object : ScreenManager(SoundBrowser::class) {
        val BROWSER = idOf("browser")

        private const val DIRECTORY_TOOLTIP = "soundboard.browser.tooltip.directory"

        private var savedOffset = 0.0
        private val collapsedPaths = mutableSetOf<String>()
        private val keybinds = mutableListOf<Pair<KeyBinding, (SoundBrowser) -> Unit>>()

        fun keyAction(key: KeyBinding, action: SoundBrowser.() -> Unit) {
            keybinds += key to action
        }
    }
}