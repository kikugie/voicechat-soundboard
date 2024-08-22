package dev.kikugie.soundboard.audio.data

import dev.kikugie.kowoui.fallbackTranslation
import dev.kikugie.soundboard.MOD_ID
import dev.kikugie.soundboard.audio.prefix
import net.minecraft.text.Text

data class SoundGroup(
    val id: SoundId,
    val entries: Map<SoundId, SoundEntry>,
    private val text: Text? = null,
) {
    val title: Text by lazy {
        text ?: id.run {
            val key = "$MOD_ID.dir.$namespace${directory.replace('/', '.').prefix(".")}"
            val fallback = str.removePrefix("$MOD_ID:")
            key.fallbackTranslation(fallback)
        }
    }

    fun isEmpty() = entries.isEmpty()
    operator fun get(id: SoundId) = entries[id]
}