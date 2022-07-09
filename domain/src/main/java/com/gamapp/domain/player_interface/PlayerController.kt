package com.gamapp.domain.player_interface

interface PlayerController {
    fun play()
    fun pause()
    fun fastForward()
    fun fastRewind()
    fun seekTo(ml: Long)
    fun setRepeatMode(mode: RepeatMode)
    fun shuffle(enable: Boolean)
}