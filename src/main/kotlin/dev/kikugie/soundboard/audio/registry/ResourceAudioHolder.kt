package dev.kikugie.soundboard.audio.registry

import dev.kikugie.soundboard.MOD_ID
import dev.kikugie.soundboard.audio.*
import dev.kikugie.soundboard.audio.data.AudioConfiguration.Companion.DEFAULT
import dev.kikugie.soundboard.audio.data.SoundEntry
import dev.kikugie.soundboard.audio.data.SoundGroup
import dev.kikugie.soundboard.audio.data.SoundId
import dev.kikugie.soundboard.util.idOf
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.allOf
import java.util.concurrent.Executor
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.seconds

object ResourceAudioHolder : SimpleResourceReloadListener<GroupMap> {
    var sounds: GroupMap = emptyMap()
        private set
    val groups get() = sounds.values.asSequence()
    val entries get() = groups.flatMap { it.entries.values.asSequence() }

    operator fun get(id: SoundId) = sounds[id.parent()][id]

    override fun getFabricId(): Identifier = idOf("sounds")
    override fun load(
        manager: ResourceManager,
        executor: Executor
    ) = supplyAsync(executor) {
        manager.findResources("soundboard") { it.path.endsWith(".$FORMAT") }
    }.composeAsync(executor) { resources ->
        val futures = resources.map { (id, file) -> supplyAsync(executor) { entry(manager, id, file) } }.toTypedArray()
        allOf(*futures).applyAsync(executor) { _ ->
            val map: MutableMap<SoundId, MutableList<SoundEntry>> = mutableMapOf()
            futures.forEach {
                val entry = it.join()
                map.getOrPut(entry.id.parent(), ::mutableListOf) += entry
            }
            map.mapValues { (path, entries) -> SoundGroup(path, entries.associateBy(SoundEntry::id)) }
        }
    }

    override fun apply(
        data: GroupMap,
        manager: ResourceManager,
        executor: Executor
    ): CompletableFuture<Void> = runAsync(executor) {
        synchronized(this) { sounds = data }
    }

    private fun entry(manager: ResourceManager, id: Identifier, file: Resource): SoundEntry {
        // Null if default, don't assign the default
        val configuration =
            manager.getResource(id.withPath { "${it.removeSuffix(FORMAT)}properties" }).getOrNull()?.let {
                val map = runCatching { PropertiesReader.read(it) }.getOrNull() ?: return@let null
                DEFAULT.cloneOrNull {
                    map["start"]?.toDoubleOrNull()?.coerceAtLeast(0.0)?.seconds?.let { start = it }
                    map["end"]?.toDoubleOrNull()?.coerceAtLeast(0.0)?.seconds?.let { end = it }
                    map["volume"]?.toIntOrNull()?.coerceIn(0, 100)?.let { volume = it / 100.0 }
                }
            }
        // example:soundboard/sound.wav -> example:/sound
        val path = id.withPath { it.removePrefix(MOD_ID).removeSuffix(".$FORMAT") }
        return SoundEntry(SoundId(path), file::getInputStream, settings = configuration)
    }
}