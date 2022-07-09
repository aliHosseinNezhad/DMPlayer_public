package com.gamapp.dmplayer.framework.player

import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import com.gamapp.domain.player_interface.PlayerController
import com.gamapp.domain.player_interface.RepeatMode


class PlayerControllerImpl(private val _controller: () -> MediaControllerCompat.TransportControls?) :
    PlayerController {

    private val controller get() = _controller()

    override fun play() {
        controller?.play()
    }

    override fun pause() {
        controller?.pause()
    }

    override fun fastForward() {
        controller?.fastForward()
    }

    override fun fastRewind() {
        controller?.fastForward()
    }

    override fun seekTo(ml: Long) {
        controller?.seekTo(ml)
    }

    override fun setRepeatMode(mode: RepeatMode) {
        controller?.setRepeatMode(mode.value)
    }

    override fun shuffle(enable: Boolean) {
        controller?.setShuffleMode(if (enable) SHUFFLE_MODE_ALL else SHUFFLE_MODE_NONE)
    }
}