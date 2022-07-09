package com.gamapp.dmplayer.framework.player

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asFlow
import com.gamapp.dmplayer.Constant
import com.gamapp.dmplayer.framework.service.MusicControllerConnectionState
import com.gamapp.dmplayer.framework.service.MusicServiceConnection
import com.gamapp.domain.models.BaseTrackModel
import com.gamapp.domain.player_interface.PlayerConnection
import com.gamapp.domain.player_interface.PlayerController
import com.gamapp.domain.player_interface.PlayerEvents
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PlayerConnectionImpl @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : PlayerConnection {
    private val mediaController: MediaControllerCompat? get() = musicServiceConnection.mediaController
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    override val controllers: PlayerControllerImpl = PlayerControllerImpl {
        mediaController?.transportControls
    }
    override val playerEvents: PlayerDataImpl = PlayerDataImpl()
    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            coroutineScope.launch {
                val ids = children.mapNotNull { mediaItem ->
                    val id = try {
                        mediaItem.mediaId?.toLong()
                    } catch (e: NumberFormatException) {
                        null
                    }
                    if (id != null)
                        BaseTrackModel(
                            id = id,
                            title = mediaItem.description.title.toString(),
                            subtitle = mediaItem.description.subtitle.toString()
                        )
                    else null
                }
                TODO()
//                callback.currentPlayList tryEmit ids
            }
        }
    }

    init {
        musicServiceConnection.subscribe(
            Constant.MUSIC_SERVICE_ROOT_ID,
            subscriptionCallback
        )
        coroutineScope.launch {
            musicServiceConnection.connectionState.asFlow().collectLatest {
                if (it is MusicControllerConnectionState.Connected) {
                    val controller = musicServiceConnection.mediaController
                    if (controller != null) {
                        playerEvents.register(controller)
                    }
                }
            }
        }
    }

    fun close() {
        val c = mediaController
        musicServiceConnection.unsubscribe(Constant.MUSIC_SERVICE_ROOT_ID, subscriptionCallback)
        if (c != null) {
            playerEvents.unregister(c)
        }
        coroutineScope.cancel()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event.targetState) {
            Lifecycle.State.INITIALIZED -> {

            }
            Lifecycle.State.CREATED -> {

            }
            Lifecycle.State.STARTED -> {

            }
            Lifecycle.State.RESUMED -> {

            }
            Lifecycle.State.DESTROYED -> {

            }
        }
    }
}