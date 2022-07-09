package com.gamapp.dmplayer.framework.player

import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.asFlow
import com.gamapp.dmplayer.Constant
import com.gamapp.dmplayer.framework.service.MusicControllerConnectionState
import com.gamapp.dmplayer.framework.service.MusicServiceConnection
import com.gamapp.domain.models.BaseTrackModel
import com.gamapp.domain.player_interface.PlayerData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import java.io.Closeable
import javax.inject.Inject

infix fun <T> MutableStateFlow<T>.tryEmit(value: T) {
    this.tryEmit(value)
}


interface PlayerConnection : Closeable {
    val callback: PlayerData
    val controller: PlayerController
}

class PlayerConnectionImpl @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    override val callback: PlayerDataImpl
) : PlayerConnection {
    private val mediaController get() = musicServiceConnection.mediaController
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    override val controller: PlayerControllerImpl = PlayerControllerImpl {
        mediaController?.transportControls
    }
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
                        callback.register(controller)
                    }
                }
            }
        }
    }

    override fun close() {
        val c = mediaController
        musicServiceConnection.unsubscribe(Constant.MUSIC_SERVICE_ROOT_ID, subscriptionCallback)
        if (c != null) {
            callback.unregister(c)
        }
        coroutineScope.cancel()
    }
}