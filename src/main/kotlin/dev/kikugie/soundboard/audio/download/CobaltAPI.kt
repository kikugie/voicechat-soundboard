@file:OptIn(DelicateCoroutinesApi::class)

package dev.kikugie.soundboard.audio.download

import dev.kikugie.kowoui.text
import dev.kikugie.kowoui.translation
import dev.kikugie.soundboard.GAME_DIR
import dev.kikugie.soundboard.LOGGER
import dev.kikugie.soundboard.Soundboard
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.Texts
import net.minecraft.util.Formatting
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URI
import java.net.URLEncoder
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.outputStream

private const val COBALT_ROOT = "soundboard.downloader.error.cobalt"
private const val REQUEST_FAIL = "soundboard.downloader.error.request_fail"
private const val COBALT_PICKER = "soundboard.downloader.error.cobalt.picker"
private const val COBALT_UNKNOWN = "soundboard.downloader.error.cobalt.unknown"
private const val COBALT_MISSING = "soundboard.downloader.error.cobalt.missing"

abstract class CobaltAPI {
    protected abstract val ENDPOINT: String
    private val CLIENT = OkHttpClient.Builder()
        .addNetworkInterceptor { chain ->
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .header(
                        "User-Agent",
                        "kikugie/soundboard/${this::class.simpleName}",
                    )
                    .build(),
            )
        }.build()

    protected val JSON = Json {
        ignoreUnknownKeys = true
    }

    fun download(url: URI, dest: Path): Job = GlobalScope.launch {
        supervisorScope {
            LOGGER.info("Querying $url")
            val link = when (val res = get(url).await()) {
                is ErrorResponse -> throw TranslatedException(res.cause)
                is StreamResponse -> res.url
            }
            LOGGER.info("Downloading to ${GAME_DIR.relativize(dest)}")
            withContext(Dispatchers.IO) {
                download(link, dest)
            }
        }
    }

    private fun download(link: String, dest: Path) {
        val request = Request.Builder().url(link).build()
        return CLIENT.newCall(request).execute().use {
            if (!it.isSuccessful) throw TranslatedException(REQUEST_FAIL.translation("${it.code}: ${it.body?.string()} (${it.message})"))
            dest.outputStream(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { output ->
                it.body!!.byteStream().use { input ->
                    input.copyTo(output)
                }
            }
        }
    }

    private fun CoroutineScope.get(url: URI): Deferred<CobaltResponse> = async {
        val body = createMetadata(url).joinToString(",", prefix = "{", postfix = "}") { (k, v) ->
            "\"$k\":\"${URLEncoder.encode(v.toString(), Charsets.UTF_8)}\""
        }
        val request = Request.Builder()
            .url(ENDPOINT)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .post(body.toRequestBody())
        Soundboard.config.cobaltToken.takeUnless { it.isBlank() }?.let {
            request.header("Authorization", "Api-Key $it")
        }
        try {
            CLIENT.newCall(request.build()).execute().use {
                if (it.body == null) throw Exception("${it.code} - '${it.message}'")
                decodeResponse(it.body!!.string().ifBlank { "\"\"" })
            }
        } catch (e: Exception) {
            ErrorResponse(REQUEST_FAIL.translation(e.message ?: "MISSING ERROR MESSAGE"))
        }
    }

    protected abstract fun createMetadata(url: URI): List<Pair<String, Any>>
    protected abstract fun decodeResponse(contents: String): CobaltResponse
}

object CobaltAPIV10 : CobaltAPI() {
    @Serializable
    private data class JsonResponse(
        val status: String,
        val url: String? = null,
        val error: CobaltError? = null,
    )

    @Serializable
    private data class CobaltError(
        val code: String,
        val context: ErrorContext? = null,
    )

    @Serializable
    private data class ErrorContext(
        val service: String? = null,
        val limit: Int? = null,
    )

    override val ENDPOINT: String
        get() = "${Soundboard.config.cobaltEndpoint}/"

    override fun createMetadata(url: URI) = listOf(
        "url" to url,
        "audioFormat" to "wav",
        "downloadMode" to "audio",
        "disableMetadata" to true
    )

    override fun decodeResponse(contents: String): CobaltResponse {
        val result = JSON.decodeFromString<JsonResponse>(contents)
        return when (result.status) {
            "tunnel", "redirect" -> StreamResponse(result.url!!)
            "picker" ->  ErrorResponse(COBALT_PICKER.translation())
            "error" -> result.let {
                val error = result.error ?: return@let ErrorResponse(COBALT_MISSING.translation())
                val text = parseError(error.code, "url" to result.url, "limit" to error.context?.limit?.toString(), "service" to error.context?.service)
                ErrorResponse(text)
            }
            else -> ErrorResponse(COBALT_UNKNOWN.translation(result.status))
        }
    }

    private fun parseError(code: String, vararg params: Pair<String, String?>): Text {
        var translated = "$COBALT_ROOT.${code.removePrefix("error.")}".translation().string
        for ((k, v) in params) if (v != null) translated = translated.replace("<$k>", v)
        return translated.text()
    }
}

object CobaltAPIV7 : CobaltAPI() {
    @Serializable
    private data class JsonResponse(
        val status: String,
        val text: String = "",
        val url: String = "",
    )

    private val HYPERLINK = Regex("<a\\b[^>]*>(.*?)</a>")
    private val HREF = Regex("href\\s*=\\s*\"(.*?)\"")
    private val TEXT = Regex(">([^<]+)<")
    override val ENDPOINT: String
        get() = "${Soundboard.config.cobaltEndpoint}/api/json"

    override fun createMetadata(url: URI) = listOf(
        "url" to url,
        "aFormat" to "wav",
        "isAudioOnly" to true,
        "disableMetadata" to true
    )

    override fun decodeResponse(contents: String): CobaltResponse {
        val result = JSON.decodeFromString<JsonResponse>(contents)
        return when(result.status) {
            "stream", "redirect" -> StreamResponse(result.url)
            "error" -> ErrorResponse(result.text.convert())
            "picker" -> ErrorResponse(COBALT_PICKER.translation())
            else -> ErrorResponse(COBALT_UNKNOWN.translation(result.status))
        }
    }

    private fun String.convert(): Text {
        val components = newlineAtBr().fixCapitalization().parseLinks()
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