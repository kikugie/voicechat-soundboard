package dev.kikugie.soundboard.audio

import dev.kikugie.soundboard.audio.data.SoundGroup
import dev.kikugie.soundboard.audio.data.SoundId
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.runAsync
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor

typealias GroupMap = Map<SoundId, SoundGroup>

val BASE_DIR: Path = FabricLoader.getInstance().configDir.resolve("soundboard")
val SUPPORTED_FORMATS = setOf("wav", "mp3")
const val FORMAT = "wav" // Primary format for backward compatibility

// Extension function to check if a path has a supported audio format
fun String.hasSupportedAudioFormat(): Boolean = SUPPORTED_FORMATS.any { this.endsWith(".$it", ignoreCase = true) }

// Extension function to get the file extension
fun String.getAudioExtension(): String? = SUPPORTED_FORMATS.find { this.endsWith(".$it", ignoreCase = true) }

internal fun runAsync(executor: Executor, action: () -> Unit) = runAsync(action, executor)
internal fun <T> supplyAsync(executor: Executor, action: () -> T): CompletableFuture<T> = supplyAsync(action, executor)
internal fun <T, R> CompletableFuture<T>.composeAsync(executor: Executor, action: (T) -> CompletionStage<R> ): CompletableFuture<R> = thenComposeAsync(action, executor)
internal fun <T, R> CompletableFuture<T>.applyAsync(executor: Executor, action: (T) -> R ): CompletableFuture<R> = thenApplyAsync(action, executor)

fun String.prefix(prefix: String) = if (isEmpty() || startsWith(prefix)) this else "$prefix$this"
operator fun SoundGroup?.get(id: SoundId) = this?.get(id)