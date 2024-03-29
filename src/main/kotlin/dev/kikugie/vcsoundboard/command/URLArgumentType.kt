package dev.kikugie.vcsoundboard.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import dev.kikugie.vcsoundboard.audio.AudioType
import net.minecraft.text.Text
import java.net.MalformedURLException
import java.net.URL
import kotlin.io.path.Path
import kotlin.io.path.extension

class URLArgumentType : ArgumentType<URL> {
    private val INVALID_URL = SimpleCommandExceptionType(Text.of("Invalid URL string"))
    private val INVALID_FORMAT = SimpleCommandExceptionType(Text.of("Unsupported file format. Only .mp3 and .wav can be used"))
    override fun parse(reader: StringReader): URL {
        val text = reader.remaining
        reader.cursor = reader.totalLength
        val url = try {
            URL(text)
        } catch (e: MalformedURLException) {
            throw INVALID_URL.create()
        }
        if (AudioType.match(Path(url.path).extension) == null)
            throw INVALID_FORMAT.create()
        return url
    }
}