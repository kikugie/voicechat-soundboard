package dev.kikugie.soundboard.entrypoint

import dev.kikugie.soundboard.MOD_ID
import dev.kikugie.soundboard.Soundboard
import dev.kikugie.soundboard.VERSION
import dev.kikugie.soundboard.audio.AudioScheduler
import dev.kikugie.soundboard.audio.StreamAudioScheduler
import net.fabricmc.api.ClientModInitializer
import su.plo.voice.api.addon.AddonInitializer
import su.plo.voice.api.addon.annotation.Addon
import su.plo.voice.api.addon.injectPlasmoVoice
import su.plo.voice.api.client.PlasmoVoiceClient
import su.plo.voice.api.client.audio.source.LoopbackSource
import su.plo.voice.api.client.event.audio.capture.AudioCaptureEvent
import su.plo.voice.api.client.event.connection.VoicePlayerConnectedEvent
import su.plo.voice.api.client.event.connection.VoicePlayerDisconnectedEvent
import su.plo.voice.api.event.EventSubscribe
import javax.sound.sampled.AudioFormat

@Addon(
    id = MOD_ID,
    version = VERSION,
    name = "Plasmo Voice Soundboard",
    authors = ["KikuGie"]
)
object PlasmoEntrypoint : SoundboardEntrypoint, AddonInitializer, ClientModInitializer {
    private val client: PlasmoVoiceClient by injectPlasmoVoice()
    private lateinit var channel: LoopbackSource

    override fun onAddonInitialize() {
        channel = client.sourceManager.createLoopbackSource(false)
    }

    override fun onInitializeClient() {
        PlasmoVoiceClient.getAddonsLoader().load(this)
        SoundboardAccess.register(this)
        Soundboard.initialize()
    }

    @EventSubscribe
    fun onConnected(event: VoicePlayerConnectedEvent) {
        scheduler.reset()
        channel.initialize(false)
    }

    @EventSubscribe
    fun onDisconnected(event: VoicePlayerDisconnectedEvent) {
        scheduler.reset()
        channel.close()
    }

    @EventSubscribe
    fun onAudioCapture(event: AudioCaptureEvent) {
        val extra = scheduler.next()
        if (extra == null || extra.isEmpty()) return
        val modifiedSamples = combineAudio(frameSize, event.samples, extra)
        modifiedSamples.copyInto(event.samples)
        channel.write(modifiedSamples)
    }

    override val format: AudioFormat
        get() = client.serverInfo.orElseThrow().voiceInfo.createFormat(false)
    override val connected: Boolean
        get() = client.serverConnection.isPresent
    override val scheduler: AudioScheduler = StreamAudioScheduler(this)

    private fun combineAudio(frameSize: Int, vararg parts: ShortArray): ShortArray = ShortArray(frameSize) { i ->
        parts
            .sumOf { it[i].toInt() }
            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }
}