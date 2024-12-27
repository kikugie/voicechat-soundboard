package dev.kikugie.soundboard.audio.download

import dev.kikugie.kowoui.access.*
import dev.kikugie.kowoui.experimental.plusAssign
import dev.kikugie.kowoui.overlay
import dev.kikugie.kowoui.text
import dev.kikugie.kowoui.translation
import dev.kikugie.kowoui.util.CombinedAlignment
import dev.kikugie.soundboard.GAME_DIR
import dev.kikugie.soundboard.LOGGER
import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.gui.widget.DownloadErrorWidget
import dev.kikugie.soundboard.util.client
import io.wispforest.owo.ui.container.StackLayout
import io.wispforest.owo.ui.core.Positioning
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.core.Surface
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import net.minecraft.client.gui.screen.ConfirmScreen
import net.minecraft.client.gui.screen.Screen
import java.lang.ref.WeakReference
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.invariantSeparatorsPathString

object Downloader {
    private const val FAILURE = "soundboard.downloader.failure"
    private const val SUCCESS = "soundboard.downloader.success"
    private const val QUIT = "soundboard.downloader.quit"
    private const val CONFIRM = "soundboard.downloader.confirm"
    private val downloads: MutableMap<Path, Pair<URI, Job>> = mutableMapOf()

    @JvmStatic fun downloads(): Map<Path, Pair<URI, Job>> = downloads
    @JvmStatic fun confirmation(parent: Screen, action: () -> Any?): ConfirmScreen {
        val title = QUIT.translation()
        val paths = downloads.keys.joinToString("\n") {
            GAME_DIR.relativize(it).invariantSeparatorsPathString
        }.let {
            CONFIRM.translation(it)
        }
        val callback: (Boolean) -> Unit = {
            if (it) action() else client.setScreen(parent)
        }
        return ConfirmScreen(callback, title, paths)
    }

    fun isDownloading(path: Path) = path in downloads

    fun download(url: URI, dest: Path, ref: WeakReference<StackLayout>) {
        if (downloads[dest]?.first == url) return // Don't repeat downloads
        val job = Soundboard.config.cobaltVersion.download(url, dest).apply {
            invokeOnCompletion {
                val file = GAME_DIR.relativize(dest)
                if (it !is CancellationException) downloads.remove(dest)
                when (it) {
                    is CancellationException -> LOGGER.info("Download cancelled for $url")
                    null -> {
                        LOGGER.info("Saved $url to $file")
                        client.player?.sendMessage(SUCCESS.translation(file.invariantSeparatorsPathString), false)
                    }

                    else -> {
                        LOGGER.error("Failed to download $url", it)
                        // TODO should open a popup if screen has been closed
                        ref.get()?.createWidget(it) ?: client.player
                            ?.sendMessage(FAILURE.translation(file.invariantSeparatorsPathString), false)
                    }
                }
            }
        }
        downloads.put(dest, url to job)?.second?.cancel("Replaced with a download for $url")
    }

    private fun StackLayout.createWidget(error: Throwable) {
        val message = if (error is TranslatedException) error.text else error.message?.text() ?: "UNKNOWN ERROR".text()
        this += overlay(DownloadErrorWidget(message)) {
            sizing = Sizing.fill(80)
            positioning = Positioning.relative(50, 50)
            alignment = CombinedAlignment.CENTER
            surface = Surface.BLANK
            zIndex = 100
        }
    }
}