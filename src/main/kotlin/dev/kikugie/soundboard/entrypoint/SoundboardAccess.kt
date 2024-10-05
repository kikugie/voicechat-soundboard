package dev.kikugie.soundboard.entrypoint

object SoundboardAccess {
    private val _delegates = mutableListOf<SoundboardEntrypoint>()
    val delegates: List<SoundboardEntrypoint> get() = _delegates
    val active: SoundboardEntrypoint? get() = delegates.firstOrNull { it.connected }

    fun register(entry: SoundboardEntrypoint) {
        _delegates.add(entry)
    }

    inline fun <T> first(selector: SoundboardEntrypoint.() -> T): T? = delegates.firstOrNull { it.connected }?.selector()
    inline fun forEach(action: SoundboardEntrypoint.() -> Unit) = delegates.forEach(action)
}