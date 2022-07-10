package com.gamapp.dmplayer.framework.player

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.gamapp.dmplayer.framework.service.MusicService
import com.gamapp.dmplayer.framework.service.MusicSource
import com.gamapp.domain.player_interface.PlayerConnection
import com.gamapp.domain.usecase.data.tracks.GetTracksByIdUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject
import javax.inject.Singleton


class PlayerConnectionImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val musicSource: MusicSource,
    private val getTracksByIdUseCase: GetTracksByIdUseCase,
) : PlayerConnection {
    private var mediaBrowser: MediaBrowserCompat? = null
    private var currentActivity: Activity? = null
    private val mediaController: MediaControllerCompat?
        get() = run {
            currentActivity?.let {
                MediaControllerCompat.getMediaController(it)
            }
        }

    override fun <T> setup(activity: T) where T : Activity, T : LifecycleOwner {
        activity.lifecycle.addObserver(this)
        currentActivity = activity
    }

    val mediaControllerCallback = object : MediaControllerCompat.Callback() {
        override fun onSessionDestroyed() {
            mediaBrowser?.disconnect()
            // maybe schedule a reconnection using a new MediaBrowser instance
        }
    }
    private val mediaBrowserConnectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            Log.i(TAG, "onConnected: ")
            mediaBrowser?.sessionToken?.also { token ->
                // Create a MediaControllerCompat
                val activity = currentActivity
                if (activity != null) {
                    val mediaController = MediaControllerCompat(
                        context, // Context
                        token
                    )
                    mediaController.registerCallback(mediaControllerCallback)
                    playerEvents.register(mediaController)
                    MediaControllerCompat.setMediaController(activity, mediaController)
                }
            }
        }

        override fun onConnectionSuspended() {

        }

        override fun onConnectionFailed() {

        }
    }

    override val playerEvents: PlayerEventImpl =
        PlayerEventImpl(getTracksByIdUseCase = getTracksByIdUseCase,
            _controller = {
                mediaController
            })
    override val controllers: PlayerControllerImpl =
        PlayerControllerImpl(
            musicSource = musicSource,
            playerEvents = playerEvents,
            _controller = {
                mediaController
            })

    private var scope: CoroutineScope? = null
        set(value) {
            playerEvents.scope = value
            controllers.scope = value
            field = value
        }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event.targetState) {
            Lifecycle.State.CREATED -> {
                val job = SupervisorJob()
                scope = CoroutineScope(Dispatchers.Main + job)
                mediaBrowser = MediaBrowserCompat(
                    context,
                    ComponentName(
                        context,
                        MusicService::class.java
                    ),
                    mediaBrowserConnectionCallback,
                    null
                )
            }
            Lifecycle.State.STARTED -> {
                mediaBrowser?.let {
                    if (!it.isConnected)
                        it.connect()
                }
            }
            Lifecycle.State.DESTROYED -> {
                mediaController?.let {
                    it.unregisterCallback(mediaControllerCallback)
                    playerEvents.unregister(it)
                }
                mediaBrowser?.let {
                    if (it.isConnected)
                        it.disconnect()
                }
                currentActivity = null
                scope?.cancel()
                scope = null
            }
            else -> {}
        }
    }
}