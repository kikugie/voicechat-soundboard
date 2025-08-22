package dev.kikugie.soundboard.audio.registry

import dev.kikugie.kowoui.text
import dev.kikugie.kowoui.translation
import dev.kikugie.soundboard.MOD_ID
import dev.kikugie.soundboard.audio.*
import dev.kikugie.soundboard.audio.data.SoundEntry
import dev.kikugie.soundboard.audio.data.SoundGroup
import dev.kikugie.soundboard.audio.data.SoundId
import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import net.minecraft.text.Text
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.io.path.PathWalkOption.BREADTH_FIRST

@OptIn(ExperimentalPathApi::class)
object FileAudioHolder {
    private val fileAccessCache: Object2LongMap<Path> = Object2LongOpenHashMap()
    var sounds: GroupMap = emptyMap()
        private set

    val groups get() = update().let { sounds.values.asSequence() }
    val entries get() = update().let { groups.flatMap { it.entries.values.asSequence() } }

    operator fun get(id: SoundId): SoundEntry? {
        // Try to find a file with any supported format
        val basePath = BASE_DIR.resolve(id.path.removePrefix("/"))
        val path = SUPPORTED_FORMATS.asSequence()
            .map { format -> runCatching { basePath.withExtension(format) }.getOrNull() }
            .firstOrNull { it?.exists() == true }
            ?: return null

        val props = path.withExtension("properties")
        val cached = cache(path) && (props.notExists() || cache(props))
        if (!cached) update()
        return sounds[id.parent()][id]
    }

    fun update() {
        if (cache(BASE_DIR)) return
        val titles: MutableMap<SoundId, Text?> = mutableMapOf()
        val buffer: MutableMap<SoundId, MutableList<SoundEntry>> = mutableMapOf()
        BASE_DIR.walk(BREADTH_FIRST).filter { path ->
            SUPPORTED_FORMATS.contains(path.extension.lowercase())
        }.forEach { path ->
            val id = BASE_DIR.relativize(path).invariantSeparatorsPathString.let {
                if ('/' !in it) "/$it" else it
            }.let { pathStr ->
                val extension = path.extension
                SoundId(MOD_ID, pathStr.removeSuffix(".$extension"))
            }
            val parentId = id.parent()
            val props = path.withExtension("properties")
            val entry = sounds[parentId][id]?.takeIf { cache(path) && (props.notExists() || cache(props)) }
                ?: entry(id, path, props)
            buffer.getOrPut(parentId, ::mutableListOf) += entry
            titles.computeIfAbsent(parentId) { title(path.parent) }
        }
        sounds = buffer.mapValues { (id, list) -> SoundGroup(id, list.associateBy(SoundEntry::id), titles[id]) }
    }

    private fun title(path: Path): Text? {
        val props = path.resolve(".properties")
        val title = runCatching { PropertiesReader.read(props)["title"]?.text() }.getOrNull()
        return when {
            title != null -> title
            path == BASE_DIR -> "soundboard.title".translation()
            else -> null
        }
    }

    private fun entry(id: SoundId, path: Path, props: Path): SoundEntry {
        val title = runCatching { PropertiesReader.read(props)["title"]?.text() }.getOrNull()
        return SoundEntry(id, path::inputStream, null, title)
    }

    private fun cache(path: Path) =
        if (path.exists() && path.isRegularFile()) path.getLastModifiedTime().toMillis()
            .let { fileAccessCache.put(path, it) == it }
        else {
            fileAccessCache.removeLong(path)
            false
        }

    private fun Path.withExtension(ext: String): Path =
        resolveSibling("${fileName.nameWithoutExtension}.$ext")
}