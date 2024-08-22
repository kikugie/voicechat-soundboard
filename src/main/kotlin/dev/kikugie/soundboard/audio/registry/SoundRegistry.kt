package dev.kikugie.soundboard.audio.registry

import dev.kikugie.kowoui.translation
import dev.kikugie.soundboard.CONFIG
import dev.kikugie.soundboard.MOD_ID
import dev.kikugie.soundboard.audio.data.SoundEntry
import dev.kikugie.soundboard.audio.data.SoundGroup
import dev.kikugie.soundboard.audio.data.SoundId

object SoundRegistry {
    lateinit var favourites: SoundGroup
        private set

    val groups get() = FileAudioHolder.groups + ResourceAudioHolder.groups
    val entries get() = FileAudioHolder.entries + ResourceAudioHolder.entries

    operator fun get(id: SoundId) = FileAudioHolder[id] ?: ResourceAudioHolder[id]

    fun update() {
        FileAudioHolder.update()
        favourites = favourites()
    }

    private fun favourites(): SoundGroup {
        val entries = CONFIG.favourites.mapNotNull(::get)
        return SoundGroup(
            SoundId(MOD_ID, "!favourites"),
            entries.associateBy(SoundEntry::id),
            "soundboard.favourites".translation()
        )
    }
}