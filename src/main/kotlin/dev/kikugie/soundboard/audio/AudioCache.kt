package dev.kikugie.soundboard.audio

import java.nio.file.Path
import java.nio.file.attribute.FileTime
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.notExists
import kotlin.time.Duration.Companion.hours

class AudioCache {
    private val cache = mutableMapOf<Path, Pair<FileTime, ShortArray>>()
    operator fun get(path: Path): ShortArray? {
        if (path.notExists()) return null
        val entry = cache[path]
        val mod = path.getLastModifiedTime()
        if (entry != null && entry.first == mod)
            return entry.second
        return convert(path, 1.hours, 1F).also { cache[path] = mod to it }
    }
    fun clear() = cache.clear()
}