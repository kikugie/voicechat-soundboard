package dev.kikugie.soundboard.gui

import com.mojang.blaze3d.systems.RenderSystem
import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.audio.AudioType
import dev.kikugie.soundboard.mixin.owo_ui.GridLayoutAccessor
import dev.kikugie.soundboard.mixin.owo_ui.ScrollContainerAccessor
import dev.kikugie.soundboard.util.asTranslation
import dev.kikugie.soundboard.util.childById
import dev.kikugie.soundboard.util.mouseDown
import io.wispforest.owo.ui.base.BaseUIModelScreen
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.container.CollapsibleContainer
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.GridLayout
import io.wispforest.owo.ui.container.ScrollContainer
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.parsing.UIModel
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Util
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.math.ceil

class SoundBrowser : BaseUIModelScreen<FlowLayout>(FlowLayout::class.java, BROWSER) {
    private var scrollbar: ScrollContainerAccessor? = null

    override fun build(root: FlowLayout) {
        populateEntries(root)
        scrollbar = root.childById<ScrollContainer<*>>("scroll") as? ScrollContainerAccessor
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
        create(Soundboard.ROOT, "soundboard.title".asTranslation().string)?.apply { container.child(this) }
        Soundboard.ROOT.listDirectoryEntries().filter {
            it.isDirectory()
        }.forEach {
            create(it)?.apply { container.child(this) }
        }
    }

    private fun create(path: Path, name: String = path.nameWithoutExtension): FlowLayout? {
        val files = path.listDirectoryEntries().filter {
            it.isRegularFile() && AudioType.match(it.extension) != null
        }.map {
            button(FILE_FORMATTER.asTranslation(it.nameWithoutExtension)).mouseDown { _, _, _ ->
                Soundboard.play(it, Screen.hasShiftDown()); true
            }
        }
        if (files.isEmpty()) return null
        val template = group()
        val label: CollapsibleContainer = template.childById("collapse") ?: return null
        label.mouseDown { _, _, b ->
            Screen.hasShiftDown().also { if (it) Util.getOperatingSystem().open(path.toFile()) }
        }
        val child = label.titleLayout().children().firstOrNull { it is LabelComponent } as? LabelComponent
        child?.text(DIRECTORY_FORMATTER.asTranslation(name))
        child?.tooltip("$DIRECTORY_FORMATTER.tooltip".asTranslation())

        val contents: GridLayout = label.childById("contents") ?: return null
        contents as GridLayoutAccessor
        val columns = contents.columns
        val rows = ceil(files.size / columns.toDouble()).toInt()
        contents.rows = rows
        contents.children = arrayOfNulls(rows * columns)
        files.forEachIndexed { i, it ->
            contents.child(it, i / columns, i % columns)
        }
        return template
    }

    private fun group(): FlowLayout = model.template("group")
    private fun button(name: Text) = TrimmedButton.from(model.template("button"), name)
    private inline fun <reified T : Component> UIModel.template(
        name: String,
        params: Map<String, String> = emptyMap(),
    ): T = this.expandTemplate(T::class.java, name, params)

    companion object {
        val BROWSER = Soundboard.id("browser")
        const val DIRECTORY_FORMATTER = "soundboard.browser.directory_name"
        const val FILE_FORMATTER = "soundboard.browser.file_name"

        private var savedOffset = 0.0

        fun open() = RenderSystem.recordRenderCall { MinecraftClient.getInstance().setScreen(SoundBrowser()) }
    }
}