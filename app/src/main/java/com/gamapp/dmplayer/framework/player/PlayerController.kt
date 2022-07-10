package com.gamapp.dmplayer.framework.player

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import android.util.Log
import com.gamapp.dmplayer.framework.service.MusicSource
import com.gamapp.dmplayer.framework.service.callback.MusicPlaybackPreparer
import com.gamapp.domain.mapper.bundle
import com.gamapp.domain.models.BaseTrack
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.player_interface.PlayerController
import com.gamapp.domain.player_interface.RepeatMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

const val TAG = "PlayerControllerTAG"

class PlayerControllerImpl(
    private val playerEvents: PlayerEventImpl,
    private val musicSource: MusicSource,
    private val _controller: () -> MediaControllerCompat?
) : PlayerController {
    var scope: CoroutineScope? = null
    private val controller get() = _controller()?.transportControls

    override fun play() {
        controller?.play()
    }

    override fun pause() {
        controller?.pause()
    }

    override suspend fun forward(ml: Long) {
        val duration = (playerEvents.duration.value).coerceAtLeast(0L)
        val target = playerEvents.currentPosition.value + abs(ml)
        val position = (target).coerceIn(0L, duration)
        controller?.seekTo(position)
    }

    override suspend fun rewind(ml: Long) {
        val duration = playerEvents.duration.value.coerceAtLeast(0L)
        val target = playerEvents.currentPosition.value - abs(ml)
        val position = (target).coerceIn(0L, duration)
        controller?.seekTo(position)
    }

    override fun skipToNext() {
        controller?.skipToNext()
    }

    override fun skipToPrevious() {
        controller?.skipToPrevious()
    }

    override suspend fun seekTo(ml: Long) {
        controller?.seekTo(ml)
        //delay in order to  make sure that position is updated by callback
        delay(300)
    }

    override fun setRepeatMode(mode: RepeatMode) {
        controller?.setRepeatMode(mode.value)
    }

    override fun shuffle(enable: Boolean) {
        controller?.setShuffleMode(if (enable) SHUFFLE_MODE_ALL else SHUFFLE_MODE_NONE)
    }

    override fun setCurrentTrack(track: BaseTrack) {
        controller?.skipToQueueItem(track.id)
    }

    override fun setPlayList(
        current: TrackModel,
        playList: List<TrackModel>,
        playWhenReady: Boolean
    ) {
        musicSource.setPlayList(playList)
        controller?.prepareFromMediaId(current.id.toString(), Bundle().apply {
            putBoolean(MusicPlaybackPreparer.PlayWhenReady, playWhenReady)
            current.bundle(this)
        })
    }
}