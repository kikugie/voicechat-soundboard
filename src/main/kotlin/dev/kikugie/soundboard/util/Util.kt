package dev.kikugie.soundboard.util

import dev.kikugie.soundboard.MOD_ID
import kotlinx.coroutines.*
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.Identifier
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.*

typealias Property<T> = KMutableProperty0<T>

fun idOf(path: String): Identifier = Identifier.of(MOD_ID, path)
fun idOf(namespace: String, path: String) = Identifier.of(namespace, path)

inline fun runOn(context: CoroutineContext, crossinline action: () -> Unit) {
    CoroutineScope(context).launch { action() }
}

inline fun ShortArray.reassign(transform: (Short) -> Short) {
    for (i in indices) this[i] = transform(this[i])
}

inline infix fun Boolean.then(action: () -> Unit): Boolean {
    if (this) action()
    return this
}

val shiftDown: Boolean get() = Screen.hasShiftDown()
val ctrlDown: Boolean get() = Screen.hasControlDown()
val altDown: Boolean get() = Screen.hasAltDown()