package dev.kikugie.vcsoundboard.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.kikugie.vcsoundboard.SoundboardConfig
import dev.kikugie.vcsoundboard.VoiceChatSoundboard
import dev.kikugie.vcsoundboard.audio.FileDownloader
import dev.kikugie.vcsoundboard.audio.FileSelector
import net.minecraft.text.Text
import net.silkmc.silk.commands.ClientCommandSourceStack
import net.silkmc.silk.commands.clientCommand
import net.silkmc.silk.commands.sendFailure
import net.silkmc.silk.commands.sendSuccess
import java.net.URL
import kotlin.io.path.inputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

fun registerCommands() {
    clientCommand("sound") {
        argument("sound", SoundboardArgumentType()) { arg ->
            runs {
                val location = arg()
                val decoder = VoiceChatSoundboard.api.createMp3Decoder(location.inputStream())
                if (decoder == null)
                    source.sendFailure(Text.of("Failed to decode file"))
                else try {
                    val data = VoiceChatSoundboard.cache[location]
//                    VoiceChatSoundboard.channel.play(data)
                    VoiceChatSoundboard.scheduler.schedule(data)
                } catch (e: Exception) {
                    VoiceChatSoundboard.LOGGER.error("Failed to play sound", e)
                    source.sendFailure(Text.of("Failed to play sound: ${e::class.simpleName}. Check game log for details"))
                }
            }
        }
    }
    clientCommand("soundboard") {
        literal("get") {
            literal("select") {
                runs {
                    FileSelector.select {
                        source.sendSuccess(Text.of("Files copied"))
                    }
                }
            }
            literal("url") {
                argument("url", URLArgumentType()) { arg ->
                    runs { download(arg(), null) }
                }
                argument("filename", StringArgumentType.string()) { arg1 ->
                    argument("url", URLArgumentType()) { arg ->
                        runs { download(arg(), arg1()) }
                    }
                }
            }
        }
        @Suppress("UNCHECKED_CAST")
        literal("config") {
            (VoiceChatSoundboard.config::class as KClass<SoundboardConfig>).declaredMemberProperties
                .filter {
                    it.isAccessible = true
                    it.getDelegate(VoiceChatSoundboard.config) is SoundboardConfig.ArgDelegate<*>
                }
                .forEach {
                    val delegate = it.getDelegate(VoiceChatSoundboard.config) as SoundboardConfig.ArgDelegate<Any>
                    literal(it.name) {
                        argument("value", delegate.arg) { arg ->
                            runs {
                                delegate.setValue(VoiceChatSoundboard.config, it, arg())
                            }
                        }
                    }
                }
        }
        literal("refresh") {
            runs {
                VoiceChatSoundboard.cache.clear()
            }
        }
        literal("cancel") {
            runs {
                VoiceChatSoundboard.scheduler.schedule(null)
            }
        }
    }
}

private fun CommandContext<ClientCommandSourceStack>.download(url: URL, filename: String?) {
    FileDownloader.download(url, filename) {
        source.sendSuccess(Text.of("Downloaded files"))
    }
}