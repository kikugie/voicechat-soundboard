package dev.kikugie.vcsoundboard.audio

import dev.kikugie.vcsoundboard.VoiceChatSoundboard
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import kotlin.io.path.getLastModifiedTime

class AudioCache {
    private val cache = mutableMapOf<Path, Pair<FileTime, ShortArray>>()
    operator fun get(path: Path): ShortArray {
        val entry = cache[path]
        val mod = path.getLastModifiedTime()
        if (entry != null && entry.first == mod)
            return entry.second

        val config = VoiceChatSoundboard.config
        return convert(path, config.maxDuration, config.volumeModifier).also { cache[path] = mod to it }
    }
    fun clear() = cache.clear()
}