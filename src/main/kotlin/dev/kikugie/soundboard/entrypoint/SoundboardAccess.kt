package dev.kikugie.soundboard.entrypoint

import dev.kikugie.soundboard.SoundRegistry
import java.util.ArrayList

object SoundboardAccess {
    private val delegates = ArrayList<SoundboardEntrypoint>()
    fun register(entry: SoundboardEntrypoint) {
        delegates.add(entry)
    }

    fun forEach(action: SoundboardEntrypoint.() -> Unit) {
        delegates.forEach(action)
    }

    fun play(local: Boolean, entry: SoundRegistry.SoundEntry) = forEach { play(local, entry) }
}