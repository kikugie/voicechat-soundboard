package dev.kikugie.soundboard.audio.registry

import net.minecraft.resource.Resource
import java.nio.file.Path
import kotlin.io.path.useLines

object PropertiesReader {
    fun read(resource: Resource) = resource.reader.useLines {
        it.map()
    }

    fun read(path: Path) = path.useLines {
        it.map()
    }

    private fun Sequence<String>.map() = mapNotNull { line ->
        if (line.isBlank()) return@mapNotNull null
        if (line.trimStart().startsWith('#')) return@mapNotNull null

        var (key, value) = line.split('=')
        key = key.trim()
        if (key.isNotEmpty()) return@mapNotNull key to value.unquote()

        var (key2, value2) = line.split(':')
        key2 = key2.trim()
        if (key2.isNotEmpty()) return@mapNotNull key2 to value2.unquote()

        return@mapNotNull null
    }.toMap()

    private fun String.unquote() = trim().trim('"', '\'')
}