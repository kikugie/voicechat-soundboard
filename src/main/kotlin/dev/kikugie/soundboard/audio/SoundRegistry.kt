package dev.kikugie.soundboard.audio

import dev.kikugie.soundboard.util.PropertiesReader
import dev.kikugie.soundboard.util.idOf
import dev.kikugie.soundboard.util.memoize
import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import kotlin.io.path.*

private typealias EntryMap = Map<String, SoundGroup>
private typealias MutableEntryMap = MutableMap<String, SoundGroup>

object SoundRegistry : SimpleResourceReloadListener<EntryMap> {
    val BASE_DIR: Path = FabricLoader.getInstance().configDir.resolve("soundboard")
    private const val FORMAT = ".wav"
    private val fileAccessCache: Object2LongMap<String> = Object2LongOpenHashMap()
    private val localEntries: MutableEntryMap = mutableMapOf()
    private var resourceEntries: EntryMap = emptyMap()

    private val EntryMap.allEntries get() = values.asSequence().flatMap(SoundGroup::entries)
    private val localCache = memoize<SoundId, _> { lookup ->
        localEntries.allEntries.firstOrNull { it.id == lookup }
    }
    private val resourceCache = memoize<SoundId, _> { lookup ->
        resourceEntries.allEntries.firstOrNull { it.id == lookup }
    }

    val groups get() = localEntries.values.asSequence() + resourceEntries.values.asSequence()
    val entries get() = localEntries.allEntries + resourceEntries.allEntries

    operator fun get(id: SoundId): SoundEntry? =
        localCache(id) ?: resourceCache(id)

    @OptIn(ExperimentalPathApi::class)
    fun update() {
        BASE_DIR
            .walk(PathWalkOption.BREADTH_FIRST, PathWalkOption.INCLUDE_DIRECTORIES)
            .filter { it.isDirectory() }
            .forEach { updatePath(it.getLocal()) }
        localCache.clear()
    }

    override fun getFabricId(): Identifier = idOf("sound_registry")

    override fun load(
        manager: ResourceManager,
        profiler: Profiler,
        executor: Executor,
    ): CompletableFuture<EntryMap> = CompletableFuture.supplyAsync({ manager.findResources("soundboard") { it.path.endsWith(FORMAT) } }, executor)
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
        { synchronized(this@SoundRegistry) { resourceEntries = data; resourceCache.clear() } }, executor
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

    internal fun splitPath(path: String): Pair<String, String> {
        var (namespace, location) = path.split(':')
        assert(namespace.isNotEmpty())
        location = location.replace('/', '.')
        return namespace to location
    }
}