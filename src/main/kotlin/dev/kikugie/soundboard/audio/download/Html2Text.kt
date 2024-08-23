package dev.kikugie.soundboard.audio.download

import dev.kikugie.kowoui.text
import net.minecraft.text.*
import net.minecraft.util.Formatting

object Html2Text {
    private val HYPERLINK = Regex("<a\\b[^>]*>(.*?)</a>")
    private val HREF = Regex("href\\s*=\\s*\"(.*?)\"")
    private val TEXT = Regex(">([^<]+)<")

    fun convert(text: String): Text {
        val components = text.newlineAtBr().fixCapitalization().parseLinks()
        return Texts.join(components, Text.empty())
    }

    private fun String.newlineAtBr() = replace("<br>", "\n")
    private fun String.fixCapitalization() = buildString {
        var capitalizeNext = true
        for (i in this@fixCapitalization.indices) {
            val char = this@fixCapitalization[i]
            if (!capitalizeNext || !char.isLetter()) append(char)
            else append(char.uppercaseChar()).also { capitalizeNext = false }

            if (char == '\n' || char.isWhitespace() && this@fixCapitalization.getOrElse(i - 1) { ' ' } in ".!?")
                capitalizeNext = true
            else if (!char.isWhitespace()) capitalizeNext = false
        }
    }

    private fun String.parseLinks(): List<Text> {
        val matches = HYPERLINK.findAll(this).toList()
        if (matches.isEmpty()) return listOf(text())

        val result = mutableListOf<Text>()
        var last = 0

        for (match in matches) {
            if (last < match.range.first) {
                val segment = substring(last, match.range.first)
                result += segment.text()
            }

            val segment = match.value
            result += segment.parseLink()
            last = match.range.last + 1
        }
        return result
    }

    private fun String.parseLink(): List<Text> {
        val href = HREF.find(this)?.groupValues?.getOrNull(1) ?: return listOf(Text.empty())
        val text = TEXT.find(this)?.groupValues?.getOrNull(1) ?: return listOf(Text.empty())
        val style = Style.EMPTY
            .withItalic(true)
            .withUnderline(true)
            .withColor(Formatting.BLUE)
            .withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, href))
        return text.text().getWithStyle(style)
    }
}