package com.gamapp.dmplayer.framework.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.gamapp.domain.Constant
import com.gamapp.domain.Constant.NETWORK_ERROR
import com.gamapp.dmplayer.framework.service.callback.MusicPlaybackPreparer
import com.gamapp.domain.ACTIONS
import com.gamapp.domain.mediaStore.MediaStoreChangeHandler
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

const val TAG = "MusicServiceTAG"

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var mediaStoreChangeHandler: MediaStoreChangeHandler

    var isForegroundService: Boolean = false
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private lateinit var musicNotificationManager: MusicServiceNotification
    lateinit var mediaSession: MediaSessionCompat
    lateinit var mediaSessionConnector: MediaSessionConnector

    private var musicPlayerHandlerThread: HandlerThread? = null
    private var playerHandler: Handler? = null
    private var mediaCategoryObserver: MediaCategoryObserver? = null

    lateinit var playbackPreparer: MusicPlaybackPreparer

    private var isPlayerInitialized = false

    @Inject
    lateinit var dataSourceFactory: DefaultDataSource.Factory


    @Inject
    lateinit var player: ExoPlayer

    @Inject
    lateinit var musicSource: MusicSource

    override fun onCreate() {
        super.onCreate()
        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

        musicPlayerHandlerThread = HandlerThread("PlaybackHandler")
        musicPlayerHandlerThread?.start()
        playerHandler = Handler(musicPlayerHandlerThread!!.looper)
        mediaCategoryObserver = MediaCategoryObserver(this, playerHandler!!)
        registerCategoryObserver(
            contentResolver = contentResolver,
            mediaCategoryObserver = mediaCategoryObserver!!
        )
        mediaSession = MediaSessionCompat(applicationContext, Constant.MEDIA_SESSION_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }
        playbackPreparer = MusicPlaybackPreparer { playWhenReady, current ->
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
        musicNotificationManager.setPlayer(null)
        serviceScope.cancel()
        player.release()
    }

    fun notifyChange(what: String) {
        if (what == ACTIONS.MEDIA_STORE_CHANGED) {
            sendBroadcast(Intent(what))
            mediaStoreChangeHandler.notifyMediaStoreChanged()
        }
    }
}