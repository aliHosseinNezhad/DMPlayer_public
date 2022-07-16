package com.gamapp.dmplayer.framework.service

import android.app.PendingIntent
import android.content.ComponentName
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.lifecycle.asFlow
import androidx.media.MediaBrowserServiceCompat
import com.gamapp.data.repository.collect
import com.gamapp.domain.Constant
import com.gamapp.domain.Constant.NETWORK_ERROR
import com.gamapp.dmplayer.framework.service.callback.MusicPlaybackPreparer
import com.gamapp.domain.ACTIONS
import com.gamapp.domain.mediaStore.MediaStoreChangeReceiver
import com.gamapp.domain.player_interface.PlayerController
import com.gamapp.domain.usecase.data.tracks.GetAllTracksUseCase
import com.gamapp.domain.usecase.player.PlayerUpdateByMediaStoreChangeUseCase
import com.gamapp.domain.usecase.player.SetCurrentTrackUseCase
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.TracksInfo
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSource
import dagger.Lazy
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
        Log.i(TAG, "onCreate: MusicService")

        bindMediaStoreChangeListenerService(serviceConnection)

        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
        mediaSession = MediaSessionCompat(applicationContext, Constant.MEDIA_SESSION_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }
        playbackPreparer = MusicPlaybackPreparer(player) { playWhenReady, current ->
            preparePlayer(
                musicSource.songs,
                current,
                playWhenReady
            )
        }
        sessionToken = mediaSession.sessionToken
        musicNotificationManager = MusicServiceNotification(
            applicationContext,
            sessionToken = mediaSession.sessionToken,
            notificationListener = MusicPlayerNotificationListener(this)
        )
        musicNotificationManager.setPlayer(player)

        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onCustomAction(action: String?, extras: Bundle?) {

            }
        })

        

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(playbackPreparer)
        mediaSessionConnector.setPlayer(player)
        mediaSessionConnector.setQueueNavigator(MediaQueueNavigator())

    }

    inner class MediaQueueNavigator : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return musicSource.songs[windowIndex]
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
            val resultsSent = musicSource.whenReady { isInitialized ->
                if (isInitialized) {
                    result.sendResult(musicSource.asMediaItems())
                    if (!isPlayerInitialized && musicSource.songs.isNotEmpty()) {
                        preparePlayer(musicSource.songs, musicSource.songs[0], false)
                        isPlayerInitialized = true
                    }
                } else {
                    mediaSession.sendSessionEvent(NETWORK_ERROR, null)
                    result.sendResult(null)
                }
            }
            if (!resultsSent) {
                result.detach()
            }
        }
    }

    private fun findCurrentIndex(
        itemToPlay: MediaDescriptionCompat?,
        songs: List<MediaDescriptionCompat>
    ): Int {
        for (i in songs.indices) {
            if (itemToPlay?.mediaId == songs[i].mediaId)
                return i
        }
        return 0
    }

    private fun preparePlayer(
        songs: List<MediaDescriptionCompat>,
        itemToPlay: MediaDescriptionCompat?,
        playNow: Boolean
    ) {
        if (songs.isEmpty()) return
        val index = findCurrentIndex(itemToPlay = itemToPlay, songs = songs)
        player.setMediaSource(musicSource.asMediaSource(dataSourceFactory))
        player.seekTo(index, 0L)
        player.prepare()
        player.playWhenReady = playNow
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: MusicService")
        unbindService(serviceConnection)
        musicNotificationManager.setPlayer(null)
        serviceScope.cancel()
        player.release()
    }


}