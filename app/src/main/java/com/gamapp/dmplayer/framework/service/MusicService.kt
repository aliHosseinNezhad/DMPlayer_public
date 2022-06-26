package com.gamapp.dmplayer.framework.service

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.gamapp.dmplayer.Constant
import com.gamapp.dmplayer.framework.player.PlayerConnectorImpl
import com.gamapp.dmplayer.framework.player.PlayerDataImpl
import com.gamapp.dmplayer.framework.player.toMediaItem
import com.gamapp.dmplayer.framework.player.toMediaMetaData
import com.gamapp.domain.ACTIONS
import com.gamapp.domain.mediaStore.MediaStoreChangeHandler
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.lang.Exception
import javax.inject.Inject


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

    @Inject
    lateinit var defaultMediaSourceFactory: DefaultDataSource.Factory

    @Inject
    lateinit var playerAccessManager: PlayerConnectorImpl

    @Inject
    lateinit var playerData: PlayerDataImpl

    @Inject
    lateinit var player: ExoPlayer


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
        sessionToken = mediaSession.sessionToken
        musicNotificationManager = MusicServiceNotification(
            applicationContext,
            sessionToken = mediaSession.sessionToken,
            notificationListener = MusicPlayerNotificationListener(this)
        )

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(player)
//        mediaSessionConnector.setPlaybackPreparer()
        mediaSessionConnector.setQueueNavigator(MediaQueueNavigator())
        musicNotificationManager.setPlayer(player)
        playerAccessManager.onCreate(player = player, serviceScope = serviceScope)
        playerData.onCreate(serviceScope = serviceScope, player = player)
    }

    inner class MediaQueueNavigator : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return playerData.playList.value.order[windowIndex].toMediaMetaData()!!.description
        }
    }

    private class MyPlaybackPreparer :
        MediaSessionConnector.PlaybackPreparer {
        override fun onCommand(
            player: Player,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun getSupportedPrepareActions(): Long {
            TODO("Not yet implemented")
        }

        override fun onPrepare(playWhenReady: Boolean) {
            TODO("Not yet implemented")
        }

        override fun onPrepareFromMediaId(
            mediaId: String,
            playWhenReady: Boolean,
            extras: Bundle?
        ) {
            TODO("Not yet implemented")
        }

        override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) {
            TODO("Not yet implemented")
        }

        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
            TODO("Not yet implemented")
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
            result.detach()
            serviceScope.launch {
                playerData.playList.collect {
                    it.order.mapNotNull { track ->
                        track.toMediaMetaData()?.toMediaItem()
                    }.let { mediaItems ->
                        try {
                            result.sendResult(mediaItems.toMutableList())
                        } catch (e: Exception) {

                        }
                    }
                }
            }
        } else {
            result.sendResult(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerAccessManager.onDestroy()
        playerData.onDestroy()
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