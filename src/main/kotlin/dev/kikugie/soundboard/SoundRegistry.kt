package dev.kikugie.soundboard

import dev.kikugie.soundboard.SoundRegistry.SoundGroup
import dev.kikugie.soundboard.audio.AudioConfiguration
import dev.kikugie.soundboard.util.*
import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceManager
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import kotlin.io.path.*

private typealias EntryMap = Map<String, SoundGroup>
private typealias MutableEntryMap = MutableMap<String, SoundGroup>

object SoundRegistry : SimpleResourceReloadListener<EntryMap> {
    val BASE_DIR = FabricLoader.getInstance().configDir.resolve("soundboard")
    private const val FORMAT = ".wav"
    private val fileAccessCache: Object2LongMap<String> = Object2LongOpenHashMap()
    private val localEntries: MutableEntryMap = mutableMapOf()
    private var resourceEntries: EntryMap = emptyMap()
    val entries get() = localEntries.values.asSequence() + resourceEntries.values.asSequence()

    @OptIn(ExperimentalPathApi::class)
    fun update() {
        BASE_DIR
            .walk(PathWalkOption.BREADTH_FIRST, PathWalkOption.INCLUDE_DIRECTORIES)
            .filter { it.isDirectory() }
            .forEach { updatePath(it.getLocal()) }
    }

    override fun getFabricId(): Identifier = idOf("sound_registry")

    override fun load(
        manager: ResourceManager,
        profiler: Profiler,
        executor: Executor,
    ): CompletableFuture<EntryMap> = CompletableFuture
        .supplyAsync({ manager.findResources("soundboard") {it.path.endsWith(FORMAT)} }, executor)
        .thenComposeAsync(
            { resources ->
                val futures = resources.map { (id, file) -> CompletableFuture.supplyAsync({ constructEntry(id, file) }, executor) }
                CompletableFuture.allOf(*futures.toTypedArray()).thenApplyAsync { _ ->
                    val map: MutableMap<String, MutableList<SoundEntry>> = mutableMapOf()
                    for (f in futures) {
                        val (path, entry) = f.join()
                        map.getOrPut(path, ::mutableListOf) += entry
                    }
                    map.mapValues { (path, entries) -> SoundGroup(path, entries) }
                }
            }, executor
        )

    override fun apply(
        data: EntryMap,
        manager: ResourceManager,
        profiler: Profiler,
        executor: Executor,
    ): CompletableFuture<Void> = CompletableFuture.runAsync(
        { synchronized(this@SoundRegistry) { resourceEntries = data } }, executor
    )

    private fun constructEntry(id: Identifier, resource: Resource): Pair<String, SoundEntry> {
        var location = id.path.removePrefix("soundboard").removePrefix("/")
        location = if ('/' in location) location.substringBeforeLast('/') else ""
        location = "${id.namespace}:$location"
        val name = id.path.substringAfterLast('/').removeSuffix(FORMAT)
        return location to SoundEntry(name, location, resource::getInputStream)
    }

    private fun updatePath(directory: String, force: Boolean = false) {
        val absolute = if (directory.isEmpty()) BASE_DIR else BASE_DIR.resolve(directory)
        if (!checkPath(absolute, directory, force)) return
        constructGroup(absolute, directory)?.also { localEntries[directory] = it }
    }

    private fun constructGroup(absolute: Path, directory: String): SoundGroup? {
        val files = absolute.listDirectoryEntries().filter { it.extension == "wav" }
        val entries = files.map {
            val name = it.fileName.nameWithoutExtension
            val properties = absolute.resolve("$name.properties")
            val title = properties.readTitle() ?: it.getLocal().removeSuffix(FORMAT)
            SoundEntry(name, directory, it::inputStream, title)
        }
        if (entries.isEmpty()) return null
        val properties = absolute.resolve(".properties")
        val title = properties.readTitle()
            ?: BASE_DIR.relativize(absolute).joinToString("/")
                .takeIf { it.isNotEmpty() }
            ?: "soundboard.title"
        return SoundGroup(directory, entries, title)
    }

    private fun checkPath(path: Path, directory: String, force: Boolean): Boolean {
        val exists = path.exists() && path.isDirectory()
        if (!exists) {
            localEntries.remove(directory)
            fileAccessCache.removeLong(directory)
            return false
        }
        val newModified = path.readAttributes<BasicFileAttributes>().lastModifiedTime().toMillis()
        val oldModified = if (!force) fileAccessCache.getLong(directory) else 0L
        fileAccessCache.put(directory, newModified)
        return newModified != oldModified || force
    }

    private fun Path.getLocal() = BASE_DIR.relativize(this).joinToString("/")

    private fun Path.readTitle() = runCatching { PropertiesReader.decode(this)["title"] }.getOrNull()

    data class SoundGroup(
        val path: String,
        val entries: List<SoundEntry>,
        val title: String? = null,
    ) {
        fun title(): Text = title?.asTranslation() ?: run {
            val (namespace, path) = splitPath(path)
            buildString {
                append("soundboard.dir")
                append(".$namespace")
                if (path.isNotEmpty()) append(".$path")
            }.asFallbackTranslation(this@SoundGroup.path)
        }
    }

    data class SoundEntry(
        val name: String,
        val path: String,
        val supplier: () -> InputStream,
        val title: String? = null,
        var settings: AudioConfiguration? = null
    ) {
        val id get() = "$path/$name"

        fun title() = title?.asTranslation() ?: run {
            var (namespace, path) = splitPath(path)
            path += ".$name"
            if (path.startsWith('.')) path = path.drop(1)
            "soundboard.file.$namespace.$path".asFallbackTranslation(name)
        }
    }

    internal fun splitPath(path: String): Pair<String, String> {
        var (namespace, location) = path.split(':')
        assert(namespace.isNotEmpty())
        location = location.replace('/', '.')
        return namespace to location
    }
}