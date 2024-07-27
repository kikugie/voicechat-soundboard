package dev.kikugie.soundboard.audio

import dev.kikugie.kowoui.translation
import java.io.InputStream

data class SoundEntry(
    val name: String,
    val path: String,
    val supplier: () -> InputStream,
    val title: String? = null,
    var settings: AudioConfiguration? = null
) {
    val id: SoundId by lazy { SoundId("$path/$name") }

    fun title() = title?.translation() ?: run {
        var (namespace, path) = SoundRegistry.splitPath(path)
        path += ".$name"
        if (path.startsWith('.')) path = path.drop(1)
        "soundboard.file.$namespace.$path".translation(name)
    }
}