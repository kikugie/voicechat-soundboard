package dev.kikugie.soundboard.audio

import dev.kikugie.kowoui.translation
import net.minecraft.text.Text
import java.io.InputStream

data class SoundEntry(
    val name: String,
    val path: String,
    val supplier: () -> InputStream,
    private val _title: String? = null,
    var settings: AudioConfiguration? = null,
) {
    val id: SoundId by lazy { SoundId("$path/$name") }
    val title: Text by lazy {
        _title?.translation() ?: run {
            var (namespace, path) = SoundRegistry.splitPath(path)
            path += ".$name"
            if (path.startsWith('.')) path = path.drop(1)
            "soundboard.file.$namespace.$path".translation(name)
        }
    }
}