package com.gamapp.dmplayer.framework.service

import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.gamapp.dmplayer.framework.player.toMediaMetaData
import com.gamapp.domain.models.TrackModel
import javax.inject.Inject
import javax.inject.Singleton


fun List<TrackModel>?.isEqual(other: List<TrackModel>?): Boolean {
    if (this == null && other == null) return true
    if (this == null || other == null) return false
    if (this.isEmpty() && other.isEmpty()) return false
    if (this.size != other.size) return false
    for (i in indices) {
        if (this[i] != other[i]) return false
    }
    return true
}

@Singleton
class MusicSource @Inject constructor() {
    var songs: List<MediaMetadataCompat> = emptyList()
        private set

    private var listener: (() -> Unit)? = null

    fun setPlayListChangeListener(scope: () -> Unit) {
        listener = scope
    }


    private fun setPlayList(list: List<MediaMetadataCompat>) {
        songs = list
        Log.i(TAG, "setPlayList: set")
        listener?.invoke()
    }

    @JvmName("setPlayList_TrackModel")
    fun setPlayList(list: List<TrackModel>) {
        setPlayList(list.map { it.toMediaMetaData() })
    }

}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}
