package dev.kikugie.soundboard.audio.download

import dev.kikugie.soundboard.GAME_DIR
import dev.kikugie.soundboard.LOGGER
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import java.net.URI
import java.nio.file.Path

object Downloader {
    private val downloads: MutableMap<Path, Pair<URI, Job>> = mutableMapOf()

    fun isDownloading(path: Path) = path in downloads

    fun download(url: URI, dest: Path) {
        if (downloads[dest]?.first == url) return // Don't repeat downloads
        val job = CobaltApi.download(url, dest).apply {
            invokeOnCompletion {
                if (it !is CancellationException) downloads.remove(dest)
                when (it) {
                    null -> LOGGER.info("Saved $url to ${GAME_DIR.relativize(dest)}")
                    is CancellationException -> LOGGER.info("Download cancelled for $url")
                    else -> LOGGER.error("Failed to download $url", it)
                }
            }
        }
        downloads.put(dest, url to job)?.second?.cancel("Replaced with a download for $url")
    }
}