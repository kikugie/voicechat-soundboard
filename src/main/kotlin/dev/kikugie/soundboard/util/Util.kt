package dev.kikugie.soundboard.util

import dev.kikugie.soundboard.MOD_ID
import kotlinx.coroutines.*
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import net.minecraft.util.math.Vec3d
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.*

typealias Property<T> = KMutableProperty0<T>

fun idOf(path: String): Identifier = Identifier.of(MOD_ID, path)
fun idOf(namespace: String, path: String) = Identifier.of(namespace, path)

@OptIn(DelicateCoroutinesApi::class)
fun runOn(context: CoroutineContext, action: suspend CoroutineScope.() -> Unit) {
    GlobalScope.launch(context, block = action)
}

inline fun ShortArray.reassign(transform: (Short) -> Short) {
    for (i in indices) this[i] = transform(this[i])
}

inline infix fun Boolean.then(action: () -> Unit): Boolean {
    if (this) action()
    return this
}

fun Class<*>.accurateName(): String = simpleName.ifEmpty {
    val parent = if (this == Any::class.java) "Object" else superclass.accurateName()
    "out $parent"
}

fun Path.resolveOrNull(string: String) = runCatching { resolve(string) }.getOrNull()

fun Path.navigate() {
    Util.getOperatingSystem().open(this)
}

val client = MinecraftClient.getInstance()

val shiftDown: Boolean get() = Screen.hasShiftDown()
val ctrlDown: Boolean get() = Screen.hasControlDown()
val altDown: Boolean get() = Screen.hasAltDown()

var currentScreen
    get() = client.currentScreen
    set(value) {
        client.setScreen(value)
    }

operator fun Vec3d.plus(other: Vec3d) = add(other)