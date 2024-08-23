package dev.kikugie.soundboard.gui.screen

import dev.kikugie.kowoui.*
import dev.kikugie.kowoui.access.*
import dev.kikugie.kowoui.dynamic.fixedSpacer
import dev.kikugie.kowoui.dynamic.wrap
import dev.kikugie.kowoui.experimental.plus
import dev.kikugie.kowoui.experimental.plusAssign
import dev.kikugie.kowoui.util.CombinedAlignment
import dev.kikugie.kowoui.dynamic.coloredTextBox
import dev.kikugie.soundboard.audio.BASE_DIR
import dev.kikugie.soundboard.audio.FORMAT
import dev.kikugie.soundboard.audio.download.Downloader
import dev.kikugie.soundboard.gui.CONFIG_PANEL
import dev.kikugie.soundboard.gui.widget.SidebarWidget
import dev.kikugie.soundboard.util.resolveOrNull
import dev.kikugie.soundboard.util.then
import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.StackLayout
import io.wispforest.owo.ui.core.*
import io.wispforest.owo.ui.core.Insets.*
import io.wispforest.owo.ui.core.Sizing.*
import net.minecraft.client.gui.screen.ConfirmLinkScreen
import net.minecraft.util.DyeColor
import org.lwjgl.glfw.GLFW
import java.lang.ref.WeakReference
import java.net.URI
import kotlin.io.path.exists

class DownloadScreen : BaseOwoScreen<StackLayout>() {
    companion object : ScreenManager(DownloadScreen::class) {
        const val FILE_PATH = "soundboard.downloader.path"
        const val URL = "soundboard.downloader.url"
        const val FOOTER = "soundboard.downloader.footer"

        const val DOWNLOAD = "soundboard.downloader.tooltip.download"
        const val OVERWRITE_PATH = "soundboard.downloader.tooltip.overwrite_path"
        const val DOWNLOADING_PATH = "soundboard.downloader.tooltip.downloading_path"
        const val INVALID_PATH = "soundboard.downloader.tooltip.invalid_path"
        const val INVALID_URL = "soundboard.downloader.tooltip.invalid_url"
    }

    private lateinit var root: StackLayout

    override fun shouldPause(): Boolean = false
    override fun createAdapter(): OwoUIAdapter<StackLayout> = OwoUIAdapter.create(this) { h, v ->
        stack {
            horizontalSizing = h
            verticalSizing = v
            padding = both(60, 30)
            alignment = CombinedAlignment.CENTER
        }
    }

    override fun build(component: StackLayout) {
        component.allowOverflow = true
        root = component + setup()
        root.childById<FlowLayout>("container")!!.apply {
            gap = 2
            this += label(FILE_PATH.translation())
            this += coloredTextBox {
                id = "path"
                maxLength = Short.MAX_VALUE.toInt()
                drawBackground = false
                color {
                    val path = it.resolvePath()
                    when {
                        path == null -> {
                            tooltipText = INVALID_PATH.translation()
                            Color.RED
                        }

                        Downloader.isDownloading(path) -> {
                            tooltipText = DOWNLOADING_PATH.translation()
                            Color.ofDye(DyeColor.YELLOW)
                        }

                        path.exists() -> {
                            tooltipText = OVERWRITE_PATH.translation()
                            Color.ofDye(DyeColor.YELLOW)
                        }

                        else -> {
                            tooltip = null
                            null
                        }
                    }
                }
            }.wrap {
                surface = Surface.PANEL_INSET
                padding = of(1)
                verticalSizing = fixed(12)
            }

            this += fixedSpacer(4)
            this += label(URL.translation())
            this += horizontalFlow {
                gap = 2
                verticalAlignment = VerticalAlignment.CENTER
                this += coloredTextBox {
                    id = "url"
                    maxLength = Short.MAX_VALUE.toInt()
                    drawBackground = false
                    color {
                        if (runCatching { URI.create(it) }.isFailure) {
                            tooltipText = INVALID_URL.translation()
                            Color.RED
                        } else {
                            tooltip = null
                            Color.ofDye(DyeColor.LIGHT_BLUE)
                        }
                    }
                }.wrap {
                    surface = Surface.PANEL_INSET
                    padding = of(1)
                    horizontalSizing = expand()
                    verticalSizing = fixed(12)
                }
                this += button(">>".text()) {
                    id = "download"
                    sizing = fixed(12)
                    tooltipText = DOWNLOAD.translation()
                    renderer = ButtonComponent.Renderer.flat(0, 0, 0)
                }
            }

            this += fixedSpacer(4)
            this += label(FOOTER.translation()) {
                horizontalSizing = fill()
                cursorStyle = CursorStyle.HAND
                onMouseDown { _, _, _ ->
                    ConfirmLinkScreen.open(this@DownloadScreen, "https://cobalt.tools/")
                    true
                }
            }
            configure(this)
        }
    }

    private fun setup() = horizontalFlow {
        gap = 2
        horizontalSizing = fill()
        surface = CONFIG_PANEL
        padding = of(5)
        this += verticalFlow {
            id = "container"
            horizontalSizing = expand()
        }
        this += SidebarWidget(this@DownloadScreen)
    }

    private fun configure(layout: FlowLayout) = with(layout) {
        val path = childById<TextBoxComponent>("path")!!
        val url = childById<TextBoxComponent>("url")!!

        url.onKeyPress { key, _, _ ->
            (key == GLFW.GLFW_KEY_ENTER) then { download(path, url) }
        }
        childById<ButtonComponent>("download")!!.onPress {
            download(path, url)
        }
    }

    private fun download(
        path: TextBoxComponent,
        url: TextBoxComponent
    ) {
        val dest = path.text.resolvePath() ?: return
        val uri = runCatching { URI.create(url.text) }.getOrNull() ?: return
        Downloader.download(uri, dest, WeakReference(this@DownloadScreen.root))
    }

    private fun String.resolvePath() = BASE_DIR.resolveOrNull("${removePrefix(".$FORMAT")}.wav")
}