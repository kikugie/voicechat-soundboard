package dev.kikugie.soundboard.gui.screen

import dev.kikugie.kowoui.*
import dev.kikugie.kowoui.access.*
import dev.kikugie.kowoui.dynamic.wrap
import dev.kikugie.kowoui.experimental.at
import dev.kikugie.kowoui.experimental.plus
import dev.kikugie.kowoui.experimental.plusAssign
import dev.kikugie.kowoui.util.CombinedAlignment
import dev.kikugie.soundboard.CONFIG
import dev.kikugie.soundboard.audio.data.SoundEntry
import dev.kikugie.soundboard.audio.data.SoundGroup
import dev.kikugie.soundboard.audio.data.SoundId
import dev.kikugie.soundboard.audio.registry.SoundRegistry
import dev.kikugie.soundboard.entrypoint.SoundboardAccess
import dev.kikugie.soundboard.gui.CONFIG_PANEL
import dev.kikugie.soundboard.gui.component.ScrollingButtonComponent
import dev.kikugie.soundboard.gui.widget.SoundSettingsWidget
import dev.kikugie.soundboard.mixin.owo_ui.ScrollContainerAccessor
import dev.kikugie.soundboard.util.ctrlDown
import dev.kikugie.soundboard.util.navigate
import dev.kikugie.soundboard.util.shiftDown
import dev.kikugie.soundboard.util.then
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.GridLayout
import io.wispforest.owo.ui.container.ScrollContainer.Scrollbar.vanilla
import io.wispforest.owo.ui.container.StackLayout
import io.wispforest.owo.ui.core.Insets.*
import io.wispforest.owo.ui.core.ParentComponent
import io.wispforest.owo.ui.core.Positioning.relative
import io.wispforest.owo.ui.core.Sizing.expand
import io.wispforest.owo.ui.core.Sizing.fill
import io.wispforest.owo.ui.core.Surface
import net.minecraft.text.Text
import java.nio.file.Path
import kotlin.math.ceil

class SoundBrowser : ModScreen() {
    companion object : ScreenManager(SoundBrowser::class) {
        private const val FILE_TOOLTIP = "soundboard.browser.tooltip.file"
        private const val DIRECTORY_TOOLTIP = "soundboard.browser.tooltip.directory"

        private var offset = 0.0
        private var collapsed: MutableSet<SoundId> = mutableSetOf()
    }

    private var favourites: ParentComponent? = null
    private var scrollbar: ScrollContainerAccessor? = null
    internal var settings: SoundSettingsWidget? = null

    fun closeSettings() {
        settings?.update()
        settings?.parent()?.let { root.removeChild(it) }
        settings = null
    }

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

    override fun build(component: StackLayout) {
        SoundRegistry.update()
        super.build(component)
    }

    override fun setup(container: FlowLayout) = verticalScroll(container) {
        id = "scroll"
        sizing = expand()
        scrollbarThickness = 10
        scrollbar = vanilla()
        surface = Surface.PANEL_INSET
        padding = bottom(1)
    }

    override fun FlowLayout.configure() {
        createFavourites(this)
        val groups = SoundRegistry.groups.mapNotNull {
            create(it, it.id.directory.isEmpty())
        }.toList()
        this += groups
    }

    private fun group(expanded: Boolean, entries: Int, title: Text) = collapsible(title, expanded) {
        id = "group"
        val columns = CONFIG.columns
        val rows = ceil(entries / columns.toFloat()).toInt()
        this += grid(rows, columns) {
            id = "contents"
            alignment = CombinedAlignment.CENTER
            padding = of(2)
            horizontalSizing = fill()
        }
    }.wrap {
        margins = both(4, 2) + right(8)
        horizontalSizing = fill()
        surface = Surface.VANILLA_TRANSLUCENT
    }

    private fun create(category: SoundGroup, keepEmpty: Boolean) = with(category) {
        if (entries.isEmpty() && !keepEmpty) return@with null
        val location = category.id.path(false)
        val buttons = entries.values.map {
            ScrollingButtonComponent(it.title) {}.apply {
                margins = of(3)
                horizontalSizing = fill(33)
                tooltipText = FILE_TOOLTIP.translation()
                onPress { _ ->
                    settings?.update()
                    if (ctrlDown) settings(it)
                    else SoundboardAccess.active?.schedule(it, shiftDown)
                }
            }
        }
        container(location, buttons)
    }

    private fun SoundGroup.container(
        location: Path?,
        buttons: List<ScrollingButtonComponent>
    ) = group(id !in collapsed, entries.size, title).apply {
        val soundId = this@container.id
        child().apply {
            onToggle { if (it) collapsed -= soundId else collapsed += soundId }
            titleLayout().apply {
                if (location != null) {
                    tooltipText = DIRECTORY_TOOLTIP.translation()
                    onMouseDown { _, _, _ -> shiftDown then location::navigate }
                }
            }
            (collapsibleChildren().first { it.id == "contents" } as GridLayout).apply {
                val columns = CONFIG.columns
                for ((i, button) in buttons.withIndex()) at(i / columns, i % columns) += button
            }
        }
    }

    private fun settings(entry: SoundEntry) {
        settings = SoundSettingsWidget(entry, SoundboardAccess.delegates.first())
        root + overlay(settings!!) {
            closeOnClick = false
            surface = CONFIG_PANEL
            sizing = fill(65)
            positioning = relative(50, 50)
            zIndex = 100
            onMouseDown { _, _, _ -> true }
        }
    }
}