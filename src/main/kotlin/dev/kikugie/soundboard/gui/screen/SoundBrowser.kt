package dev.kikugie.soundboard.gui.screen

import dev.kikugie.kowoui.*
import dev.kikugie.kowoui.access.*
import dev.kikugie.kowoui.dynamic.wrap
import dev.kikugie.kowoui.experimental.at
import dev.kikugie.kowoui.experimental.plus
import dev.kikugie.kowoui.experimental.plusAssign
import dev.kikugie.kowoui.util.CombinedAlignment
import dev.kikugie.soundboard.CONFIG
import dev.kikugie.soundboard.ModKeyBinds
import dev.kikugie.soundboard.audio.SoundEntry
import dev.kikugie.soundboard.audio.SoundGroup
import dev.kikugie.soundboard.audio.SoundRegistry
import dev.kikugie.soundboard.entrypoint.SoundboardAccess
import dev.kikugie.soundboard.gui.component.ScrollingButtonComponent
import dev.kikugie.soundboard.gui.widget.SidebarWidget
import dev.kikugie.soundboard.gui.widget.SoundSettingsWidget
import dev.kikugie.soundboard.mixin.owo_ui.ScrollContainerAccessor
import dev.kikugie.soundboard.util.ctrlDown
import dev.kikugie.soundboard.util.shiftDown
import dev.kikugie.soundboard.util.then
import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.container.CollapsibleContainer
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.GridLayout
import io.wispforest.owo.ui.container.ScrollContainer.Scrollbar.vanilla
import io.wispforest.owo.ui.container.StackLayout
import io.wispforest.owo.ui.core.Insets.*
import io.wispforest.owo.ui.core.OwoUIAdapter
import io.wispforest.owo.ui.core.ParentComponent
import io.wispforest.owo.ui.core.Sizing.expand
import io.wispforest.owo.ui.core.Sizing.fill
import io.wispforest.owo.ui.core.Surface
import net.minecraft.text.Text
import net.minecraft.util.Util
import kotlin.io.path.exists
import kotlin.math.ceil

class SoundBrowser : BaseOwoScreen<StackLayout>() {
    companion object : ScreenManager(SoundBrowser::class) {
        private const val FILE_TOOLTIP = "soundboard.browser.tooltip.file"
        private const val DIRECTORY_TOOLTIP = "soundboard.browser.tooltip.directory"

        private var offset = 0.0
        private var collapsed: MutableSet<String> = mutableSetOf()
    }

    private lateinit var root: StackLayout
    private var favourites: ParentComponent? = null
    private var scrollbar: ScrollContainerAccessor? = null
    internal var settings: SoundSettingsWidget? = null

    fun closeSettings() {
        settings?.update()
        settings?.parent()?.let { root.removeChild(it) }
        settings = null
    }

    @JvmOverloads
    fun createFavourites(container: FlowLayout = root.childById<FlowLayout>("container")!!) {
        favourites?.let { container.removeChild(it) }
        favourites = create(SoundRegistry.favourites, false)
        favourites?.let { container.at(0) += it }
    }

    override fun init() {
        super.init()
        scrollbar?.invokeScrollBy(offset, true, false)
    }

    override fun close() {
        offset = scrollbar?.scrollOffset ?: 0.0
        settings?.update()
        super.close()
    }

    override fun shouldPause(): Boolean = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val result = super.keyPressed(keyCode, scanCode, modifiers)
        if (!result) ModKeyBinds.invoke(this)
        return result
    }

    override fun createAdapter(): OwoUIAdapter<StackLayout> = OwoUIAdapter.create(this) { h, v ->
        stack {
            horizontalSizing = h
            verticalSizing = v
            padding = both(60, 30)
            alignment = CombinedAlignment.CENTER
        }
    }

    override fun build(component: StackLayout) {
        SoundRegistry.update()
        root = component + setup()
        root.childById<FlowLayout>("container")!!.apply {
            createFavourites(this)
            this += SoundRegistry.groups.mapNotNull {
                create(it, it.path.isEmpty())
            }.toList()
        }
    }

    private fun setup() = horizontalFlow {
        horizontalSizing = fill()
        surface = Surface.PANEL
        padding = of(5)
        this += verticalScroll(verticalFlow {
            id = "container"
            horizontalSizing = expand()
        }) {
            id = "scroll"
            sizing = fill()
            scrollbarThickness = 10
            scrollbar = vanilla()
            surface = Surface.PANEL_INSET
        }
        this += SidebarWidget(this@SoundBrowser)
    }

    private fun group(expanded: Boolean, entries: Int, title: Text) = collapsible(title, expanded) {
        id = "group"
        val columns = CONFIG.columns
        val rows = ceil(entries / columns.toFloat()).toInt()
        this += grid(rows, columns) {
            id = "contents"
            alignment = CombinedAlignment.CENTER
            padding = of(2)
            sizing = fill()
        }
    }.wrap {
        margins = both(4, 2) + right(8)
        horizontalSizing = fill()
        surface = Surface.VANILLA_TRANSLUCENT
    }

    private fun create(category: SoundGroup, keepEmpty: Boolean) = with(category) {
        if (entries.isEmpty() && !keepEmpty) return@with null
        val location = runCatching { SoundRegistry.BASE_DIR.resolve(path).takeIf { it.exists() } }.getOrNull()
        val buttons = entries.map {
            ScrollingButtonComponent(it.title) {}.apply {
                margins = of(3)
                horizontalSizing = fill(33)
                tooltipText = FILE_TOOLTIP.translation()
                onPress { _ ->
                    settings?.update()
                    if (ctrlDown) settings(it)
                    else SoundboardAccess.play(it, shiftDown)
                }
            }
        }
        val group = group(path !in collapsed, entries.size, title)
        group.childById<CollapsibleContainer>("group")!!.apply {
            onToggle { if (it) collapsed += path else collapsed -= path }
            titleLayout().apply {
                if (location != null) {
                    tooltipText = DIRECTORY_TOOLTIP.translation()
                    onMouseDown { _, _, _ -> shiftDown then { Util.getOperatingSystem().open(path) } }
                }
            }
            childById<GridLayout>("contents")!!.apply {
                val columns = CONFIG.columns
                for ((i, button) in buttons.withIndex()) at(i / columns, i % columns) += button
            }
        }
        group
    }

    private fun settings(entry: SoundEntry) {
        settings = SoundSettingsWidget(entry, SoundboardAccess.delegates.first())
        root + overlay(settings!!) {
            sizing = fill(50)
            closeOnClick = false
            surface = Surface.PANEL
            positioning = io.wispforest.owo.ui.core.Positioning.relative(50, 50)
            zIndex = 100
            onMouseDown { _, _, _ -> true }
        }
    }
}