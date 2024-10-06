package dev.kikugie.soundboard.config

import dev.kikugie.soundboard.LOGGER
import dev.kikugie.soundboard.audio.data.SoundId
import dev.kikugie.soundboard.config.AudioProviderType.STREAM
import dev.kikugie.soundboard.config.CobaltAPIVersion.V10
import dev.kikugie.soundboard.util.runOn
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.fabricmc.loader.api.FabricLoader
import kotlin.io.path.*

@Serializable
@OptIn(ExperimentalSerializationApi::class)
class SoundboardConfig(
    @SerialName("dark_mode")
    var dark: Boolean = false,
    @SerialName("board_columns")
    var columns: Int = 3,
    @SerialName("audio_provider")
    var provider: AudioProviderType = STREAM,
    @SerialName("audio_volume")
    var volume: Double = 1.0,
    @SerialName("cobalt_api_endpoint")
    val cobalt: String = "https://api.cobalt.tools",
    @SerialName("cobalt_api_version")
    val version: CobaltAPIVersion = V10,
    @SerialName("favourites")
    val favourites: MutableList<SoundId> = mutableListOf(),
) {
    fun save() = runOn(Dispatchers.IO) {
        try {
            file.createParentDirectories()
            file.outputStream().use { json.encodeToStream(this, it) }
        } catch (e: Exception) {
            LOGGER.error("Failed to save config $file", e)
        }
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