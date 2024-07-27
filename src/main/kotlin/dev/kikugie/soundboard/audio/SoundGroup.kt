package dev.kikugie.soundboard.audio

import dev.kikugie.kowoui.fallbackTranslation
import dev.kikugie.kowoui.translation
import net.minecraft.text.Text

data class SoundGroup(
    val path: String,
    val entries: List<SoundEntry>,
    private val name: String? = null,
) {
    fun isEmpty() = entries.isEmpty()
    val title: Text by lazy {
        name?.translation() ?: run {
            val (namespace, path) = SoundRegistry.splitPath(path)
            buildString {
                append("soundboard.dir")
                append(".$namespace")
                if (path.isNotEmpty()) append(".$path")
            }.fallbackTranslation(this@SoundGroup.path)
        }
    }
}