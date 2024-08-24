package dev.kikugie.soundboard.audio.data

import dev.kikugie.kowoui.fallbackTranslation
import dev.kikugie.soundboard.MOD_ID
import dev.kikugie.soundboard.audio.prefix
import net.minecraft.text.Text
import java.io.InputStream

data class SoundEntry(
    val id: SoundId,
    val supplier: () -> InputStream,
    val settings: AudioConfiguration? = null,
    private val text: Text? = null,
) {
    val group: SoundId by lazy { id.parent() }
    val title: Text by lazy {
        text ?: id.run {
            // example:soundboard/sound.wav -> soundboard.file.example.sound
            "$MOD_ID.file.$namespace${id.path.replace('/', '.').prefix(".")}".fallbackTranslation(file)
        }
    }
}