package dev.kikugie.soundboard.config

import dev.kikugie.soundboard.LOGGER
import dev.kikugie.soundboard.audio.SchedulerType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.fabricmc.loader.api.FabricLoader
import kotlin.io.path.*

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class SoundboardConfig(
    var schedulerType: SchedulerType = SchedulerType.STREAM,
) {
    fun save() = try {
        file.createParentDirectories()
        file.outputStream().use { json.encodeToStream(this, it) }
    } catch (e: Exception) {
        LOGGER.error("Failed to save config $file", e)
    }

    companion object Loader {
        val file = FabricLoader.getInstance().configDir.resolve("soundboard.json")
        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
            encodeDefaults = true
        }

        fun load(): SoundboardConfig {
            if (file.exists()) try {
                return file.inputStream().use(json::decodeFromStream)
            } catch (e: Exception) {
                LOGGER.error("Failed to read config $file", e)
            }
            return SoundboardConfig().apply(SoundboardConfig::save)
        }
    }
}