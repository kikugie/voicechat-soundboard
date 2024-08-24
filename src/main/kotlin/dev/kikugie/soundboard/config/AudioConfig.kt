package dev.kikugie.soundboard.config

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import dev.kikugie.soundboard.audio.data.AudioConfiguration
import dev.kikugie.soundboard.audio.data.SoundEntry
import dev.kikugie.soundboard.audio.data.SoundId
import dev.kikugie.soundboard.util.runOn
import kotlinx.coroutines.Dispatchers
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.exists
import kotlin.io.path.isReadable
import kotlin.io.path.reader
import kotlin.io.path.writeText

private typealias ConfigEntries = MutableMap<SoundId, AudioConfiguration>

object AudioConfig {
    private val configurations: ConfigEntries = mutableMapOf()
    private val file: Path = FabricLoader.getInstance().configDir.resolve("soundboard_tracks.json")
    // Uses gson because kotlinx serialization can't handle plain maps
    private val json = GsonBuilder()
        .setPrettyPrinting()
        .setLenient()
        .create()

    init {
        load()
    }

    operator fun get(entry: SoundEntry): AudioConfiguration? = get(entry.id)
    operator fun get(id: SoundId): AudioConfiguration? = configurations[id]
    operator fun set(entry: SoundEntry, configuration: AudioConfiguration) = set(entry.id, configuration)
    operator fun set(id: SoundId, configuration: AudioConfiguration) {
        configurations[id] = configuration
        runOn(Dispatchers.IO) { save() }
    }
    operator fun minusAssign(entry: SoundEntry) = minusAssign(entry.id)
    operator fun minusAssign(id: SoundId) {
        configurations.remove(id)
        runOn(Dispatchers.IO) { save() }
    }

    fun load() {
        if (file.exists() && file.isReadable()) file.reader().use {
            val token: TypeToken<MutableMap<String, AudioConfiguration>> = TypeToken.getParameterized(Map::class.java, String::class.java, AudioConfiguration::class.java) as TypeToken<MutableMap<String, AudioConfiguration>>
            val conf = json.fromJson(it, token).mapKeys { e -> SoundId(e.key) }
            configurations.clear()
            configurations.putAll(conf)
        }
    }

    fun save() {
        file.writeText(json.toJson(configurations.mapKeys { it.key.str }), Charsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    }
}