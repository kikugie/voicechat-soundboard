package dev.kikugie.soundboard.entrypoint

import java.nio.file.Path
import java.util.ArrayList

object SoundboardAccess {
    private val delegates = ArrayList<SoundboardEntrypoint>()
    fun register(entry: SoundboardEntrypoint) {
        delegates.add(entry)
    }

    val available: SoundboardEntrypoint?
        get() = delegates.firstOrNull(SoundboardEntrypoint::connected)
    fun play(file: Path) {
        available?.play(file)
    }
}