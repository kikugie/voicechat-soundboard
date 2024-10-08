package dev.kikugie.soundboard.config

import dev.kikugie.soundboard.LOGGER
import dev.kikugie.soundboard.audio.data.SoundId
import dev.kikugie.soundboard.config.AudioProviderType.STREAM
import dev.kikugie.soundboard.config.CobaltAPIVersion.V7
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
    @JvmField @SerialName("dark_mode")
    var dark: Boolean = false,
    @JvmField @SerialName("board_columns")
    var columns: Int = 3,
    @JvmField @SerialName("audio_provider")
    var provider: AudioProviderType = STREAM,
    @JvmField @SerialName("audio_volume")
    var volume: Double = 1.0,
    @JvmField @SerialName("cobalt_api_endpoint")
    var cobaltEndpoint: String = "https://api.cobalt.tools",
    @JvmField @SerialName("cobalt_api_version")
    var cobaltVersion: CobaltAPIVersion = V7,
    @JvmField @SerialName("cobalt_api_token")
    var cobaltToken: String = "",
    @JvmField @SerialName("favourites")
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
        @JvmStatic val file = FabricLoader.getInstance().configDir.resolve("soundboard.json")
        @JvmStatic val json = Json {
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