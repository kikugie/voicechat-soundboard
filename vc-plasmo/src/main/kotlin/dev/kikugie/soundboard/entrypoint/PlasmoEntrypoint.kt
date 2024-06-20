package dev.kikugie.soundboard.entrypoint

import dev.kikugie.soundboard.MOD_ID
import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.VERSION
import dev.kikugie.soundboard.audio.AudioScheduler
import net.fabricmc.api.ClientModInitializer
import su.plo.voice.api.addon.AddonInitializer
import su.plo.voice.api.addon.annotation.Addon
import su.plo.voice.api.addon.injectPlasmoVoice
import su.plo.voice.api.client.PlasmoVoiceClient
import su.plo.voice.api.client.event.audio.capture.AudioCaptureEvent
import su.plo.voice.api.client.event.connection.VoicePlayerConnectedEvent
import su.plo.voice.api.client.event.connection.VoicePlayerDisconnectedEvent
import su.plo.voice.api.event.EventSubscribe
import java.nio.file.Path
import javax.sound.sampled.AudioFormat

@Addon(
    id = MOD_ID,
    version = VERSION,
    name = "Plasmo Voice Soundboard",
    authors = ["KikuGie"]
)
object PlasmoEntrypoint : SoundboardEntrypoint, AddonInitializer, ClientModInitializer {
    private val client: PlasmoVoiceClient by injectPlasmoVoice()

    override fun onAddonInitialize() {
    }

    override fun onInitializeClient() {
        PlasmoVoiceClient.getAddonsLoader().load(this)
        SoundboardAccess.register(this)
        Soundboard.initialize()
    }

    @EventSubscribe
    fun onConnected(event: VoicePlayerConnectedEvent) {
        scheduler.reset()
    }

    @EventSubscribe
    fun onDisconnected(event: VoicePlayerDisconnectedEvent) {
        scheduler.reset()
    }

    @EventSubscribe
    fun onAudioCapture(event: AudioCaptureEvent) {
        val extra = scheduler.next()
        if (extra == null || extra.isEmpty()) return
        val modifiedSamples = combineAudio(frameSize, event.samples, extra)
        modifiedSamples.copyInto(event.samples)
    }

    override val format: AudioFormat
        get() = client.serverInfo.orElseThrow().voiceInfo.createFormat(false)
    override val connected: Boolean
        get() = client.serverConnection.isPresent
    override val scheduler: AudioScheduler = AudioScheduler(this)

    private fun combineAudio(frameSize: Int, vararg parts: ShortArray): ShortArray = ShortArray(frameSize) { i ->
        parts
            .sumOf { it[i].toInt() }
            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }
}