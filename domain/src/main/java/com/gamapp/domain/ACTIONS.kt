package com.gamapp.domain

object ACTIONS {
    private const val APPLICATION_NAME = "com.gamapp.musicplayer"
    const val MEDIA_STORE_CHANGED = APPLICATION_NAME + "MEDIA_STORE_CHANGED"

    object PLAYER {
        const val SHOW = APPLICATION_NAME + "SHOW_TRACK_PLAYER"
        const val HIDE = APPLICATION_NAME + "HIDE_TRACK_PLAYER"
    }
}
