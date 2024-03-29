package dev.kikugie.vcsoundboard

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import dev.kikugie.vcsoundboard.command.DurationArgumentType
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class SoundboardConfig {
    inner class ArgDelegate<T>(
        var value: T,
        val arg: ArgumentType<T>
    ) : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            this.value = value
        }
    }


    var maxDuration: Duration by ArgDelegate(5.seconds, DurationArgumentType(50.milliseconds..1.hours))
//    var maxFileSize: Int by ArgDelegate(5 * 1024 * 1024, IntegerArgumentType.integer()) // Kilobytes
    var downloadTimeout: Duration by ArgDelegate(5.seconds, DurationArgumentType(1.seconds..1.hours))
    var volumeModifier: Float by ArgDelegate(1F, FloatArgumentType.floatArg(0F, 1F))
}