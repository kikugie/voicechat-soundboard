package dev.kikugie.soundboard.audio.download

import dev.kikugie.soundboard.GAME_DIR
import dev.kikugie.soundboard.LOGGER
import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.audio.BASE_DIR
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URI
import java.net.URLEncoder
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.outputStream

@OptIn(DelicateCoroutinesApi::class)
object CobaltApi {
    private val ENDPOINT get() = Soundboard.config.cobalt
    private val CLIENT = OkHttpClient.Builder()
        .addNetworkInterceptor { chain ->
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .header(
                        "User-Agent",
                        "kikugie/soundboard/CobaltApi",
                    )
                    .build(),
            )
        }.build()
    private val JSON = Json {
        ignoreUnknownKeys = true
    }

    fun download(url: URI, dest: Path): Job = GlobalScope.launch {
        supervisorScope {
            LOGGER.info("Querying $url")
            val link = when (val res = get(url).await()) {
                is ErrorResponse -> throw Exception(res.message)
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
            if (!it.isSuccessful) throw Exception("${it.code}: ${it.body?.string()} (${it.message})")
            dest.outputStream(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { output ->
                it.body!!.byteStream().use { input ->
                    input.copyTo(output)
                }
            }

        }
    }

    private fun CoroutineScope.get(url: URI): Deferred<CobaltResponse> = async {
        val link = "$ENDPOINT/api/json"
        val body = listOf(
            "url" to url.toString(),
            "aFormat" to "wav",
            "isAudioOnly" to "true",
            "disableMetadata" to "true"
        ).joinToString(",", prefix = "{", postfix = "}") { (k, v) ->
            "\"$k\":\"${URLEncoder.encode(v, Charsets.UTF_8)}\""
        }
        val request = Request.Builder()
            .url(link)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .post(body.toRequestBody())
        val result = call(request).onFailure {
            return@async ErrorResponse(it.message ?: "Failed to download")
        }.getOrThrow()
        if (result.status == "stream") StreamResponse(result.url)
        else ErrorResponse(result.text)
    }

    private fun call(request: Request.Builder) = runCatching {
        CLIENT.newCall(request.build()).execute().use {
            if (it.body == null) throw Exception("Request failed: ${it.code} - '${it.message}'")
            val contents = it.body!!.string().ifBlank { "\"\"" }
            JSON.decodeFromString<JsonResponse>(contents)
        }
    }

    @Serializable
    private data class JsonResponse(
        val status: String,
        val text: String = "",
        val url: String = "",
    )
}