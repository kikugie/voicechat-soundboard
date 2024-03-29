package dev.kikugie.vcsoundboard.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.text.Text
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class DurationArgumentType(private val range: ClosedRange<Duration>) : ArgumentType<Duration> {
    constructor(min: Duration) : this(min..Duration.INFINITE)
    constructor(min: Duration, max: Duration) : this(min..max)

    private val INVALID_UNIT = SimpleCommandExceptionType(Text.of("Invalid time unit"))
    private val OUT_OF_BOUNDS = SimpleCommandExceptionType(Text.of("Value out of bounds"))
    private val units = arrayOf("ticks", "milliseconds", "seconds", "minutes", "hours", "days")

    override fun parse(reader: StringReader): Duration {
        var result: Duration = Duration.ZERO
        while (reader.canRead()) {
            val value = reader.readInt()
            val unit = reader.readUnquotedString()
            result += parseDuration(value, unit)
            if (reader.canRead()) reader.expect(' ')
        }

        if (result !in range)
            throw OUT_OF_BOUNDS.create()
        return result
    }

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        val reader = StringReader(builder.input)
        reader.cursor = builder.start

        val usedUnits = mutableSetOf<String>()
        while (reader.canRead()) {
            val value = try {
                reader.readInt()
            } catch (e: IllegalArgumentException) {
                return Suggestions.empty()
            }
            val lastCursor = reader.cursor
            val duration: Duration
            try {
                val unit = reader.readUnquotedString()
                duration = parseDuration(value, unit)
                usedUnits += unit
            } catch (e: CommandSyntaxException) {
                return CommandSource.suggestMatching(
                    units.filter { it !in usedUnits },
                    builder.createOffset(lastCursor)
                )
            }
            if (reader.canRead()) reader.expect(' ')
        }
        return if (reader.peek(-1).isWhitespace()) {
            return builder.createOffset(reader.cursor)
                .apply { (1..9).forEach { suggest(it) } }.buildFuture()
        } else Suggestions.empty()
    }


    private fun parseDuration(value: Int, unit: String) = when (unit) {
        "milliseconds" -> value.milliseconds
        "seconds" -> value.seconds
        "minutes" -> value.minutes
        "hours" -> value.hours
        "days" -> value.days
        "ticks" -> value.milliseconds * 50
        else -> throw INVALID_UNIT.create()
    }
}