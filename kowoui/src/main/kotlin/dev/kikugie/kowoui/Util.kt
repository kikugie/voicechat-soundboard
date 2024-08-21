@file:Suppress("unused")

package dev.kikugie.kowoui

import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.Insets
import io.wispforest.owo.ui.core.ParentComponent
import io.wispforest.owo.ui.parsing.UIModel
import net.minecraft.text.Text
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal inline fun unsupported(reason: () -> String = { "" }): Nothing =
    throw UnsupportedOperationException(reason())

fun String.text(): Text = Text.of(this)
fun String.translation(vararg args: String): Text = Text.translatable(this, *args)
fun String.fallbackTranslation(fallback: String, vararg args: Any): Text = Text.translatableWithFallback(this, fallback, *args)

inline fun <reified T : Component> ParentComponent.childById(id: String): T? =
    childById(T::class.java, id)

inline fun <reified T : Component> UIModel.template(
    name: String,
    params: Map<String, String> = emptyMap(),
): T = expandTemplate(T::class.java, name, params)

operator fun Insets.plus(other: Insets): Insets = add(other.top, other.bottom, other.left, other.right)

fun <T> cached(value: T, consumer: (T) -> Unit) = Cached(value, consumer)

class Cached<T>(private var value: T, private val consumer: (T) -> Unit) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, new: T) {
        if (value == new) return
        value = new
        consumer(value)
    }
}