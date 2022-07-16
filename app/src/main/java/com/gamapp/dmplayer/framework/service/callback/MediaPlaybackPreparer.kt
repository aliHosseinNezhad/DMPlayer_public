package com.gamapp.dmplayer.framework.service.callback

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.gamapp.dmplayer.framework.player.toMediaDescription
import com.gamapp.dmplayer.framework.player.toMediaMetaData
import com.gamapp.domain.mapper.getTrackModel
import com.gamapp.domain.models.TrackModel
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector

class MusicPlaybackPreparer(
    private val player:Player,
    private val playerPrepared: (playWhenReady: Boolean, currentMediaItem: MediaDescriptionCompat?) -> Unit
) : MediaSessionConnector.PlaybackPreparer {

    companion object {
        const val PlayWhenReady = "PlayWhenReady"
    }

    override fun onCommand(
        player: Player,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?
    ): Boolean = false

    override fun getSupportedPrepareActions(): Long {
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
    }

    override fun onPrepare(playWhenReady: Boolean) {
        player.playWhenReady = playWhenReady
    }

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        val media = extras.getTrackModel()?.toMediaDescription()
        val play = extras?.getBoolean(PlayWhenReady, playWhenReady) ?: playWhenReady
        playerPrepared(play,media)
    }

    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) = Unit

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) = Unit
}
