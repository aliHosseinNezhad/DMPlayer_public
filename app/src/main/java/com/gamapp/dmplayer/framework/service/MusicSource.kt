package com.gamapp.dmplayer.framework.service

import android.media.MediaMetadata
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import android.util.Log
import androidx.core.net.toUri
import com.gamapp.dmplayer.framework.player.toMediaDescription
import com.gamapp.dmplayer.framework.player.toMediaItem
import com.gamapp.dmplayer.framework.player.toMediaMetaData
import com.gamapp.dmplayer.framework.service.State.*
import com.gamapp.domain.models.TrackModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicSource @Inject constructor() {

    var songs = emptyList<MediaDescriptionCompat>()

    fun setPlayList(allSongs: List<TrackModel>) {
        state = STATE_INITIALIZING
        songs = allSongs.mapNotNull { song ->
            song.toMediaDescription()
        }
        state = STATE_INITIALIZED
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSource.Factory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val uri = song.mediaUri
            if (uri != null) {
                val mediaSource =
                    ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                        MediaItem.fromUri(uri)
                    )
                concatenatingMediaSource.addMediaSource(mediaSource)
            }
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song ->
        MediaBrowserCompat.MediaItem(song, FLAG_PLAYABLE)
    }.toMutableList()

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        if (state == STATE_CREATED || state == STATE_INITIALIZING) {
            onReadyListeners += action
            return false
        } else {
            action(state == STATE_INITIALIZED)
            return true
        }
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}
