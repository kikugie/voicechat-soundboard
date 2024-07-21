package dev.kikugie.soundboard.gui

import dev.kikugie.soundboard.entrypoint.SoundboardAccess
import dev.kikugie.soundboard.mixin.owo_ui.GridLayoutAccessor
import dev.kikugie.soundboard.mixin.owo_ui.ScrollContainerAccessor
import dev.kikugie.soundboard.SoundRegistry
import dev.kikugie.soundboard.util.*
import io.wispforest.owo.ui.base.BaseUIModelScreen
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.container.CollapsibleContainer
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.GridLayout
import io.wispforest.owo.ui.container.ScrollContainer
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.parsing.UIModel
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.option.KeyBinding
import net.minecraft.text.Text
import net.minecraft.util.Util
import kotlin.math.ceil

class SoundBrowser : BaseUIModelScreen<FlowLayout>(FlowLayout::class.java, BROWSER) {
    private var scrollbar: ScrollContainerAccessor? = null

    override fun build(root: FlowLayout) {
        SoundRegistry.update()
        populateEntries(root)
        scrollbar = root.childById<ScrollContainer<*>>("scroll") as? ScrollContainerAccessor
        for (it in root.all()) it.keyPress { key, scan, _ ->
            val found = keybinds.firstOrNull { it.first.matchesKey(key, scan) }
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

    private fun populateEntries(root: FlowLayout) {
        val container: FlowLayout = root.childById("container")!!
        container.children(SoundRegistry.entries.mapNotNull(::create).toList())
    }

    private fun create(group: SoundRegistry.SoundGroup): FlowLayout? {
        if (group.entries.isEmpty()) return null
        val path = group.path
        val buttons = group.entries.map { entry ->
            button(entry.title(group)) { SoundboardAccess.play(Screen.hasShiftDown(), entry) }
        }
        val template = group()
        val label: CollapsibleContainer = template.childById("collapse") ?: return null
        val location = runCatching { SoundRegistry.BASE_DIR.resolve(path).toFile() }.getOrNull()
        val locationExists = location?.exists() == true
        if (locationExists) label.mouseDown { _, _, _ ->
            Screen.hasShiftDown().also { if (it) Util.getOperatingSystem().open(location) }
        }
        val child = label.titleLayout().children().firstOrNull { it is LabelComponent } as? LabelComponent
        if (locationExists) child?.tooltip("soundboard.browser.directory_name.tooltip".asTranslation())
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

    private fun group(): FlowLayout = model.template("group")
    private fun button(name: Text, onPress: (ButtonComponent) -> Unit): ButtonComponent =
        Components.button(name, onPress).apply {
            val temp: ButtonComponent = model.template("button")
            this as Component

            renderer(temp.renderer())
            textShadow(temp.textShadow())
            active(temp.active())
            cursorStyle(temp.cursorStyle())
            positioning(temp.positioning().get())
            margins(temp.margins().get())
            horizontalSizing(temp.horizontalSizing().get())
            verticalSizing(temp.verticalSizing().get())
            tooltip(temp.tooltip())
            zIndex(temp.zIndex())
        }

    private inline fun <reified T : Component> UIModel.template(
        name: String,
        params: Map<String, String> = emptyMap(),
    ): T = this.expandTemplate(T::class.java, name, params)

    companion object : ScreenManager(SoundBrowser::class) {
        val BROWSER = idOf("browser")

        private var savedOffset = 0.0
        private val collapsedPaths = mutableSetOf<String>()
        private val keybinds = mutableListOf<Pair<KeyBinding, (SoundBrowser) -> Unit>>()

        fun keyAction(key: KeyBinding, action: (SoundBrowser) -> Unit) {
            keybinds += key to action
        }
    }
}