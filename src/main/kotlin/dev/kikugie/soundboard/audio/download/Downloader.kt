package dev.kikugie.soundboard.audio.download

import dev.kikugie.kowoui.access.*
import dev.kikugie.kowoui.experimental.plusAssign
import dev.kikugie.kowoui.overlay
import dev.kikugie.kowoui.util.CombinedAlignment
import dev.kikugie.soundboard.GAME_DIR
import dev.kikugie.soundboard.LOGGER
import dev.kikugie.soundboard.gui.widget.DownloadErrorWidget
import io.wispforest.owo.ui.container.StackLayout
import io.wispforest.owo.ui.core.Positioning
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.core.Surface
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import java.lang.ref.WeakReference
import java.net.URI
import java.nio.file.Path

object Downloader {
    private val downloads: MutableMap<Path, Pair<URI, Job>> = mutableMapOf()

    fun isDownloading(path: Path) = path in downloads

    fun download(url: URI, dest: Path, ref: WeakReference<StackLayout>) {
        if (downloads[dest]?.first == url) return // Don't repeat downloads
        val job = CobaltApi.download(url, dest).apply {
            invokeOnCompletion {
                if (it !is CancellationException) downloads.remove(dest)
                when (it) {
                    null -> LOGGER.info("Saved $url to ${GAME_DIR.relativize(dest)}")
                    is CancellationException -> LOGGER.info("Download cancelled for $url")
                    else -> {
                        LOGGER.error("Failed to download $url", it)
                        // TODO should open a popup if screen has been closed
                        ref.get()?.createWidget(it)
                    }
                }
            }
        }
        downloads.put(dest, url to job)?.second?.cancel("Replaced with a download for $url")
    }

    private fun StackLayout.createWidget(error: Throwable) {
        val message = Html2Text.convert(error.message!!)
        this += overlay(DownloadErrorWidget(message)) {
            sizing = Sizing.fill(80)
            positioning = Positioning.relative(50, 50)
            alignment = CombinedAlignment.CENTER
            surface = Surface.BLANK
            zIndex = 100
        }
    }
}