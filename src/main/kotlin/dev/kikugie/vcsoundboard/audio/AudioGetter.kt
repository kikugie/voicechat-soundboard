package dev.kikugie.vcsoundboard.audio

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import de.maxhenkel.voicechat.api.audiochannel.AudioPlayer
import dev.kikugie.vcsoundboard.VoiceChatSoundboard
import kotlinx.coroutines.*
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.io.path.*


private val pending = Collections.synchronizedList<String>(mutableListOf())

@Synchronized
private fun runIfNotPending(id: String, action: () -> Unit): Boolean {
    if (id in pending) return false
    pending += id
    action()
    pending -= id
    return true
}

class OperationPendingException(message: String = "") : RuntimeException(message)

interface FileAccessor {
    fun accept(name: String, data: InputStream) {
        val path = VoiceChatSoundboard.ROOT.resolve(name)
        path.outputStream(StandardOpenOption.CREATE_NEW).use(data::copyTo)
    }
}

object FileSelector : FileAccessor {
    private val formats = arrayOf("mp3", "wav")
    fun select(action: (e: Exception?) -> Unit = {}) {
        val filters: PointerBuffer
        var selected: String?
        MemoryStack.stackPush().use { stack ->
            filters = stack.mallocPointer(formats.size)
            for (format in formats) {
                filters.put(stack.UTF8("*.$format"))
            }
            filters.flip()
            selected = TinyFileDialogs.tinyfd_openFileDialog(
                "Select files to add",
                null,
                filters,
                "Audio file",
                true
            )
        }

        if (selected == null) return //TODO
        val files = selected!!.split('|').map(::Path)
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                files.forEach {
                    val ran = runIfNotPending(it.nameWithoutExtension) {
                        it.inputStream().use { f -> accept(it.name, f) }
                    }
                    if (!ran) action(OperationPendingException())
                }
                action(null)
            }
        }
    }
}

object FileDownloader : FileAccessor {
    fun download(url: URL, name: String? = null, action: (e: Exception?) -> Unit = {}) {
        val dest = Path(url.path).let { if (name == null) it.name else "$name.${it.extension}" }
        val ran = runIfNotPending(dest.substringAfterLast('.')) {
            CoroutineScope(Dispatchers.IO).launch {
                val result = try {
                    withTimeout(VoiceChatSoundboard.config.downloadTimeout) { url.openConnection().getInputStream() }
                } catch (e: Exception) {
                    action(e)
                    return@launch
                }
                result.use { accept(dest, it) }
                action(null)
            }
        }
        if (!ran) action(OperationPendingException())
    }
}

// Unfinished
//object FilebinDownloader : FileAccessor {
//    fun download(url: URL, action: (e: Exception?) -> Unit = {}) {
//        val client = HttpClient.newHttpClient()
//        val request = HttpRequest.newBuilder(URI.create(url.toString()))
//            .header("Accept", "application/json")
//            .build()
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val result = try {
//                withTimeout(VoiceChatSoundboard.config.downloadTimeout) {
//                    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
//
//                    if (response.statusCode() != 200) action(IOException("$url responded with status ${response.statusCode()}")).also { return@launch }
//
//                    val json = JsonParser.parseString(response.body())
//                    if (json !is JsonObject) action(IOException("Invalid response")).also { return@withTimeout }
//
//                    val files = (json as JsonObject)["files"]
//                    if (files == null || files !is JsonArray) action(IOException("No files uploaded")).also { return@launch }
//
//                    (files as JsonArray).forEach {
//                        if (it !is JsonObject) return@forEach
//                        val type = it["content-type"].asString
//
//                        if (type == "audio/wav" || type == "audio/mpeg") {
//                            val size= it["bytes"].asLong
//
//                            val filename = it["filename"].asString
//                            return@forEach
//                        }
//                    }    }
//            } catch (e: Exception) {
//                action(e)
//                return@launch
//            }
//        }
//    }
//}