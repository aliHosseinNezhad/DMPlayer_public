package com.gamapp.dmplayer.framework.service

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

sealed interface MusicControllerConnectionState {
    object Connected : MusicControllerConnectionState
    object NotConnected : MusicControllerConnectionState
    object ConnectionSuspended : MusicControllerConnectionState
    object ConnectionFailed : MusicControllerConnectionState
}

@Singleton
class MusicServiceConnection @Inject constructor(
    @ApplicationContext private val context: Context
) : LifecycleEventObserver {

    private val _connectionState: MutableLiveData<MusicControllerConnectionState> =
        MutableLiveData(MusicControllerConnectionState.NotConnected)

    val connectionState: LiveData<MusicControllerConnectionState> = _connectionState

    var mediaController: MediaControllerCompat? = null
        private set

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)


    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser?.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser?.unsubscribe(parentId, callback)
    }

    private var mediaBrowser: MediaBrowserCompat? = null

    val transportControls: MediaControllerCompat.TransportControls? get() = mediaController?.transportControls

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser!!.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            _connectionState.postValue(MusicControllerConnectionState.Connected)
        }

        override fun onConnectionSuspended() {
            _connectionState.postValue(MusicControllerConnectionState.ConnectionSuspended)
            mediaController = null
        }

        override fun onConnectionFailed() {
            _connectionState.postValue(MusicControllerConnectionState.ConnectionFailed)
            mediaController = null
        }
    }


    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event.targetState) {
            Lifecycle.State.CREATED -> {
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
            Lifecycle.State.STARTED -> {
                mediaBrowser?.connect()
            }
            Lifecycle.State.DESTROYED -> {
                mediaBrowser?.disconnect()
            }
            else -> {}
        }
    }
}