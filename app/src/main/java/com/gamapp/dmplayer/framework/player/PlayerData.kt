package com.gamapp.dmplayer.framework.player

import com.gamapp.domain.player_interface.PlayerData
import com.gamapp.domain.player_interface.RepeatMode
import com.gamapp.domain.player_interface.emit
import com.gamapp.domain.models.PlayList
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.player_interface.toRepeatMode
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.text.ExoplayerCuesDecoder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton



class CancelableCoroutine(scope: CoroutineScope) : CoroutineScope by scope {
    private var job: Job? = null
    fun start(scope: suspend () -> Unit) {
        try {
            job?.cancel()
            job = launch {
                scope()
            }
        } catch (e: CancellationException) {

        }
    }

    fun stop() {
        try {
            job?.cancel()
        } catch (e: CancellationException) {
        }
    }

}


@Singleton
class PlayerDataImpl @Inject constructor() : PlayerData {
    private var player: ExoPlayer? = null
    private val listener: Player.Listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
                isPlaying emit player.isPlaying
                cancelableCoroutine.start {
                    while (player.isPlaying) {
                        delay(100)
                        duration emit player.duration
                        currentPosition emit player.currentPosition
                    }
                }
            }
            val id = player.currentMediaItem?.let {
                it.mediaId.toLongOrNull() ?: -1
            }
            val currentMusicItem = playList.value.tracks.find { it.id == id }
            currentTrack emit (currentMusicItem)
            duration emit player.duration
            currentPosition emit player.currentPosition
            shuffle emit player.shuffleModeEnabled

        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            playList emit playList.value.copy(shuffle = shuffleModeEnabled)
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            this@PlayerDataImpl.repeatMode emit repeatMode.toRepeatMode()
        }
    }
    private lateinit var scope: CoroutineScope
    private lateinit var cancelableCoroutine: CancelableCoroutine

    fun onCreate(serviceScope: CoroutineScope, player: ExoPlayer) {
        scope = serviceScope
        cancelableCoroutine = CancelableCoroutine(scope)
        setPlayer(player)
    }

    private fun setPlayer(player: ExoPlayer) {
        player.addListener(listener)
        this.player = player
    }

    fun onDestroy() {
        try {
            player?.removeListener(listener)
            listeners.forEach {
                player?.removeListener(it)
            }
        } catch (e: Exception) {
        }
    }

    override fun addListener(listener: Player.Listener) {
        player?.let {
            listeners += listener
            it.addListener(listener)
        }
    }

    private val listeners = mutableListOf<Player.Listener>()
    val concatenatingMediaSource = ConcatenatingMediaSource()
    override val isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val duration: MutableStateFlow<Long> = MutableStateFlow(0L)
    override val currentPosition: MutableStateFlow<Long> = MutableStateFlow(0L)
    override val shuffle: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val progress: Flow<Float> = duration.combine(currentPosition, transform = { a, b ->
        try {
            b / a.toFloat()
        } catch (e: Exception) {
            0f
        }
    })
    override val repeatMode: MutableStateFlow<RepeatMode> = MutableStateFlow(RepeatMode.OFF)

    override val currentTrack: MutableStateFlow<TrackModel?> = MutableStateFlow(null)
    override val playList: MutableStateFlow<PlayList> =
        MutableStateFlow(PlayList(emptyList()))
}