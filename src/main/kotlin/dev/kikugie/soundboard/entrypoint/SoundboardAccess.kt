package dev.kikugie.soundboard.entrypoint

import java.nio.file.Path
import java.util.ArrayList

object SoundboardAccess {
    private val delegates = ArrayList<SoundboardEntrypoint>()
    fun register(entry: SoundboardEntrypoint) {
        delegates.add(entry)
    }

    fun forEach(action: SoundboardEntrypoint.() -> Unit) {
        delegates.forEach(action)
    }

    fun play(file: Path) = forEach { play(file) }
}