package com.gamapp.dmplayer.framework.player

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.gamapp.dmplayer.framework.service.MediaPlayerService
import com.gamapp.dmplayer.framework.service.MusicSource
import com.gamapp.domain.player_interface.PlayerConnection
import com.gamapp.domain.usecase.data.tracks.GetTracksByIdUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.lang.IllegalStateException
import javax.inject.Inject

fun MediaBrowserCompat?.tryConnect(){
    this?.let {
        try {
            if (!it.isConnected){
                this.connect()
            }
        } catch (e:IllegalStateException){
            e.printStackTrace()
        }
    }

}
fun MediaBrowserCompat?.tryDisconnect(){
    this?.let {
        try {
            if (this.isConnected){
                this.disconnect()
            }
        } catch (e:IllegalStateException){
            e.printStackTrace()
        }
    }
}

class PlayerConnectionImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val musicSource: MusicSource,
    private val getTracksByIdUseCase: GetTracksByIdUseCase,
) : PlayerConnection {
    companion object {
        const val TAG = "PlayerConnectionTAG"
    }

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
            Log.i("MusicServiceTAG", "inside connection callback  onConnectionSuspended")
        }

        override fun onConnectionFailed() {
            Log.i("MusicServiceTAG", "inside connection callback  onConnectionFailed")
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
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                Log.i(TAG, "onStateChanged: OnCreate")
                val job = SupervisorJob()
                scope = CoroutineScope(Dispatchers.Main + job)
                mediaBrowser = MediaBrowserCompat(
                    context,
                    ComponentName(
                        context,
                        MediaPlayerService::class.java
                    ),
                    mediaBrowserConnectionCallback,
                    null
                )
            }
            Lifecycle.Event.ON_START -> {
                mediaBrowser.tryConnect()
            }
            Lifecycle.Event.ON_STOP -> {
                mediaController?.let {
                    it.unregisterCallback(mediaControllerCallback)
                    playerEvents.unregister(it)
                }
                mediaBrowser.tryDisconnect()
                currentActivity = null
                scope?.cancel()
                scope = null
            }
            else -> {}
        }
    }
}