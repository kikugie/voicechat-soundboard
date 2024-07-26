package dev.kikugie.soundboard.util

import dev.kikugie.soundboard.MOD_ID
import kotlinx.coroutines.*
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.*

typealias Property<T> = KMutableProperty0<T>

fun idOf(path: String): Identifier = Identifier.of(MOD_ID, path)
fun idOf(namespace: String, path: String) = Identifier.of(namespace, path)

fun String.asText(): Text = Text.of(this)
fun String.asTranslation(vararg args: String): Text = Text.translatable(this, *args)
fun String.asFallbackTranslation(fallback: String, vararg args: String): Text = Text.translatableWithFallback(this, fallback, args)

inline fun runOn(context: CoroutineContext, crossinline action: () -> Unit) {
    CoroutineScope(context).launch { action() }
}

inline fun ShortArray.reassign(transform: (Short) -> Short) {
    for (i in indices) this[i] = transform(this[i])
}