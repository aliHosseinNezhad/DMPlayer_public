package com.gamapp.dmplayer.framework.player

import android.support.v4.media.session.MediaControllerCompat


interface PlayerController {
    fun play()
    fun pause()
    fun fastForward()
    fun fastRewind()
}
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
}