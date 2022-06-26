package com.gamapp.domain

object ACTIONS {
    private const val APPLICATION_NAME = "com.gamapp.musicplayer"
    const val MEDIA_STORE_CHANGED = APPLICATION_NAME + "MEDIA_STORE_CHANGED"

    object PLAYER {
        const val SHOW = APPLICATION_NAME + "SHOW_TRACK_PLAYER"
        const val HIDE = APPLICATION_NAME + "HIDE_TRACK_PLAYER"
    }
    object SORT {
        object TYPE {
            const val ARTIST = APPLICATION_NAME + "ARTIST_SORT_BY"
            const val ALBUM = APPLICATION_NAME + "ALBUM_SORT_BY"
            const val TRACK = APPLICATION_NAME + "TRACK_SORT_BY"
        }
        object By {
            const val DATE_ADD = APPLICATION_NAME + "DATE_ADDED"
            const val NAME = APPLICATION_NAME + "NAME"
            const val NUMBER_OF_TRACKS = APPLICATION_NAME + "NUMBER_OF_TRACKS"
        }
    }
}
