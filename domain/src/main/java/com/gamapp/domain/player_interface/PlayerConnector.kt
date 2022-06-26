package com.gamapp.domain.player_interface


import com.gamapp.domain.models.TrackModel
import com.google.android.exoplayer2.ExoPlayer

interface PlayerConnector {
    /**
     * play
     * */
    suspend fun play()
    suspend fun pause()
    suspend fun setRepeatMode(mode: RepeatMode)
    suspend fun shuffleMode(enable: Boolean)

    /**
     * navigation
     * */
    suspend fun seekTo(seek: Long)
    suspend fun seekTo(index: Int, seek: Long)
    suspend fun stop()
    suspend fun nextMusic()
    suspend fun previousMusic()

    /**
     * playlist
     * */
    suspend fun setPlayList(playList: List<TrackModel>, onSetCurrentMusic: suspend (ExoPlayer) -> Unit = {})
    suspend fun setPlayListAndPlay(track: TrackModel, playList: List<TrackModel>)
    suspend fun setPlayerListAndCurrent(track: TrackModel, playList: List<TrackModel>)
    suspend fun removePlayListItems(items: List<Long>)
}