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
const val FORMAT = "wav"

internal fun runAsync(executor: Executor, action: () -> Unit) = runAsync(action, executor)
internal fun <T> supplyAsync(executor: Executor, action: () -> T): CompletableFuture<T> = supplyAsync(action, executor)
internal fun <T, R> CompletableFuture<T>.composeAsync(executor: Executor, action: (T) -> CompletionStage<R> ): CompletableFuture<R> = thenComposeAsync(action, executor)
internal fun <T, R> CompletableFuture<T>.applyAsync(executor: Executor, action: (T) -> R ): CompletableFuture<R> = thenApplyAsync(action, executor)

fun String.prefix(prefix: String) = if (isEmpty() || startsWith(prefix)) this else "$prefix$this"
operator fun SoundGroup?.get(id: SoundId) = this?.get(id)