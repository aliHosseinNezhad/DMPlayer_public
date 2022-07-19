package com.gamapp.dmplayer.framework.service

import android.app.PendingIntent
import android.content.ComponentName
import android.content.ServiceConnection
import android.media.browse.MediaBrowser.MediaItem.FLAG_PLAYABLE
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.compose.ui.util.fastMap
import androidx.media.MediaBrowserServiceCompat
import com.gamapp.dmplayer.framework.player.toMediaMetaData
import com.gamapp.domain.Constant
import com.gamapp.dmplayer.framework.service.callback.MusicPlaybackPreparer
import com.gamapp.domain.mapper.toTrackModel
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.usecase.player.PlayerUpdateByMediaStoreChangeUseCase
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

const val TAG = "MusicServiceTAG"

@AndroidEntryPoint
class MediaPlayerService : MediaBrowserServiceCompat() {

    var isForegroundService: Boolean = false
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)


    @Inject
    lateinit var playerUpdate: PlayerUpdateByMediaStoreChangeUseCase


    private lateinit var musicNotificationManager: MusicServiceNotification
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private lateinit var playbackPreparer: MusicPlaybackPreparer

    private var isPlayerInitialized = false

    @Inject
    lateinit var dataSourceFactory: DefaultDataSource.Factory


    @Inject
    lateinit var player: ExoPlayer

    @Inject
    lateinit var musicSource: MusicSource

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {}
        override fun onServiceDisconnected(name: ComponentName?) {}
    }


    override fun onCreate() {
        super.onCreate()
        bindMediaStoreChangeListenerService(serviceConnection)
        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
        mediaSession = MediaSessionCompat(applicationContext, Constant.MEDIA_SESSION_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }
        sessionToken = mediaSession.sessionToken
        musicNotificationManager = MusicServiceNotification(
            applicationContext,
            sessionToken = mediaSession.sessionToken,
            notificationListener = MusicPlayerNotificationListener(this)
        )
        musicNotificationManager.setPlayer(player)
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(player)
        val navigator = MediaQueueNavigator()
        musicSource.setPlayListChangeListener {
            notifyChildrenChanged(Constant.MUSIC_SERVICE_ROOT_ID)
        }
        playbackPreparer =
            MusicPlaybackPreparer(player, playerPrepared = { playWhenReady, current ->

                val currentMedia =
                    musicSource.songs.find { it.description.mediaId?.toLongOrNull() == current }
                Log.i(TAG, "playerPrepared $currentMedia")
                Log.i(TAG, "playerPrepared songs:${musicSource.songs}")
                preparePlayer(
                    musicSource.songs,
                    currentMedia,
                    playWhenReady
                )
            })
        mediaSessionConnector.setPlaybackPreparer(playbackPreparer)
        mediaSessionConnector.setQueueNavigator(navigator)
    }

    inner class MediaQueueNavigator : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return musicSource.songs[windowIndex].description
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ) = BrowserRoot(Constant.MUSIC_SERVICE_ROOT_ID, null)


    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        if (parentId == Constant.MUSIC_SERVICE_ROOT_ID) {
            result.sendResult(musicSource.songs.map {
                MediaBrowserCompat.MediaItem(
                    it.description,
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                )
            }.toMutableList())
        }
    }

    private fun findCurrentIndex(
        itemToPlay: MediaMetadataCompat?,
        songs: List<MediaMetadataCompat>
    ): Int {
        for (i in songs.indices) {
            if (itemToPlay?.description?.mediaId == songs[i].description.mediaId)
                return i
        }
        return 0
    }

    private fun List<MediaMetadataCompat>.asMediaSource(dataSourceFactory: DefaultDataSource.Factory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        this.forEach { song ->
            val uri = song.description.mediaUri
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

    private fun preparePlayer(
        songs: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playNow: Boolean
    ) {
        if (songs.isEmpty()) return
        val index = findCurrentIndex(itemToPlay = itemToPlay, songs = songs)
        player.setMediaSource(musicSource.songs.asMediaSource(dataSourceFactory))
        player.seekTo(index, 0L)
        player.prepare()
        player.playWhenReady = playNow
        Log.i(TAG, "preparePlayer: play")
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: MusicService")
        unbindService(serviceConnection)
        musicNotificationManager.setPlayer(null)
        musicSource.setPlayList(emptyList<TrackModel>())
        mediaSession.run {
            isActive = false
            release()
        }
        serviceScope.cancel()
        player.release()
    }


}