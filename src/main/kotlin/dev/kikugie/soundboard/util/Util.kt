package dev.kikugie.soundboard.util

import dev.kikugie.soundboard.MOD_ID
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.coroutines.CoroutineContext

fun modId(path: String): Identifier = Identifier.of(MOD_ID, path)

fun String.asText(): Text = Text.of(this)
fun String.asTranslation(vararg args: String): Text = Text.translatable(this, *args)

inline fun runOn(context: CoroutineContext, crossinline action: () -> Unit) {
    runBlocking { withContext(context) { action() } }
}