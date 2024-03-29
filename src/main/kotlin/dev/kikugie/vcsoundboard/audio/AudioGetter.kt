package dev.kikugie.vcsoundboard.audio

import dev.kikugie.vcsoundboard.VoiceChatSoundboard
import kotlinx.coroutines.*
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.io.InputStream
import java.net.URL
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.io.path.*
import kotlin.time.Duration.Companion.seconds

private val pending = Collections.synchronizedList<String>(mutableListOf())
private inline fun runIfNotPending(id: String, action: () -> Unit): Boolean {
    if (id in pending) return false
    pending += id
    action()
    pending -= id
    return true
}

interface FileAccessor {
    fun accept(name: String, data: InputStream) {
        val path = VoiceChatSoundboard.ROOT.resolve(name)
        path.outputStream(StandardOpenOption.CREATE_NEW).use(data::copyTo)
    }
}

object FileSelector : FileAccessor {
    private val formats = arrayOf("mp3", "wav")
    fun select(action: () -> Unit = {}) {
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
                    runIfNotPending(it.nameWithoutExtension) {it.inputStream().use { f -> accept(it.name, f) } }
                }
                action()
            }
        }
    }
}

object FileDownloader : FileAccessor {
    fun download(url: URL, name: String? = null, action: () -> Unit = {}) {
        val dest = Path(url.path).let { if (name == null) it.name else "$name.${it.extension}" }
        runIfNotPending(dest.substringAfterLast('.')) {
            CoroutineScope(Dispatchers.IO).launch {
                val result = withTimeoutOrNull(5.seconds) {
                    val connection = url.openConnection()
                    connection.getInputStream()
                }
                if (result == null) return@launch
                result.use { accept(dest, it) }
                action()
            }
        }
    }
}