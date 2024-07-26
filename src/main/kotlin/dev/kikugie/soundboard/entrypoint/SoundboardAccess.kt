package dev.kikugie.soundboard.entrypoint

import dev.kikugie.soundboard.SoundRegistry

object SoundboardAccess {
    private val _delegates = mutableListOf<SoundboardEntrypoint>()
    val delegates: List<SoundboardEntrypoint> get() = _delegates
    fun register(entry: SoundboardEntrypoint) {
        _delegates.add(entry)
    }

    inline fun <T> first(selector: SoundboardEntrypoint.() -> T): T? = delegates.firstOrNull { it.connected }?.selector()
    inline fun any(selector: SoundboardEntrypoint.() -> Boolean) = delegates.any(selector)
    inline fun all(selector: SoundboardEntrypoint.() -> Boolean) = delegates.all(selector)
    inline fun forEach(action: SoundboardEntrypoint.() -> Unit) = delegates.forEach(action)

    fun play(entry: SoundRegistry.SoundEntry, local: Boolean) = forEach { scheduleStream(entry, local) }
}