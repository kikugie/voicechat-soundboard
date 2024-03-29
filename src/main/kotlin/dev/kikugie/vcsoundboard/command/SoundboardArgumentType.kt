package dev.kikugie.vcsoundboard.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.kikugie.vcsoundboard.VoiceChatSoundboard
import dev.kikugie.vcsoundboard.audio.AudioType
import net.minecraft.command.CommandSource
import net.minecraft.text.Text
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import kotlin.io.path.*

class SoundboardArgumentType : ArgumentType<Path> {
    private val NO_FILE = SimpleCommandExceptionType(Text.of("File not found"))
    override fun parse(reader: StringReader): Path {
        val text = reader.remaining
        reader.cursor = reader.totalLength
        return files().firstOrNull { it.nameWithoutExtension == text } ?: throw NO_FILE.create()
    }

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> =
        CommandSource.suggestMatching(files().map(Path::nameWithoutExtension).asIterable(), builder)

    @OptIn(ExperimentalPathApi::class)
    private fun files() =
        if (VoiceChatSoundboard.ROOT.isDirectory()) VoiceChatSoundboard.ROOT.walk().filter { AudioType.match(it.extension) != null } else emptySequence()
}