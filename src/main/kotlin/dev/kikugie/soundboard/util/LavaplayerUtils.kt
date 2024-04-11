package dev.kikugie.soundboard.util

import com.sedmelluq.discord.lavaplayer.format.AudioPlayerInputStream
import com.sedmelluq.discord.lavaplayer.format.Pcm16AudioDataFormat
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.kikugie.soundboard.Soundboard
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import java.util.concurrent.TimeUnit
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream

sealed interface LoadResult
data class Single(val track: AudioTrack) : LoadResult
data class Playlist(val playlist: Iterable<AudioTrack>) : LoadResult
data class LoadFailed(val exception: FriendlyException) : LoadResult
data object NoMatch : LoadResult

private val MANAGER: AudioPlayerManager = DefaultAudioPlayerManager()
private val PLAYER: AudioPlayer = MANAGER.createPlayer()

fun CoroutineScope.load(source: String): Deferred<LoadResult> {
    val result = CompletableDeferred<LoadResult>()
    MANAGER.loadItem(source, object : AudioLoadResultHandler {
        override fun trackLoaded(track: AudioTrack) {
            result.complete(Single(track))
        }

        override fun playlistLoaded(playlist: AudioPlaylist) {
            result.complete(Playlist(playlist.tracks))
        }

        override fun noMatches() {
            result.complete(NoMatch)
        }

        override fun loadFailed(exception: FriendlyException) {
            result.complete(LoadFailed(exception))
        }
    })
    return result
}

fun AudioTrack.convert(target: AudioFormat): AudioInputStream {
    val format = Pcm16AudioDataFormat(target.channels, target.sampleRate.toInt(), 2, false)
    return AudioPlayerInputStream.createStream(PLAYER, format, 5000L, false)
}