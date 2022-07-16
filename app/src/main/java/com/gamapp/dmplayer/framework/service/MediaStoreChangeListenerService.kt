package com.gamapp.dmplayer.framework.service

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asFlow
import com.gamapp.data.repository.collect
import com.gamapp.domain.ACTIONS
import com.gamapp.domain.mediaStore.MediaStoreChangeReceiver
import com.gamapp.domain.usecase.data.DataUpdateByMediaStoreChangeUseCase
import com.gamapp.domain.usecase.interacts.TrackInteracts
import com.gamapp.domain.usecase.player.PlayerUpdateByMediaStoreChangeUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject


@AndroidEntryPoint
class MediaStoreChangeListenerService : Service() {
    @Inject
    lateinit var mediaStoreChangeReceiver: MediaStoreChangeReceiver

    @Inject
    lateinit var trackInteracts: TrackInteracts

    @Inject
    lateinit var playerUpdate: PlayerUpdateByMediaStoreChangeUseCase

    @Inject lateinit var localTracks :DataUpdateByMediaStoreChangeUseCase



    private val getTracks get() = trackInteracts.get



    private val mediaStoreChange = object : MediaStoreChangeListener {
        override fun onMediaStoreChange() {
            sendBroadcast(Intent(ACTIONS.MEDIA_STORE_CHANGED))
            mediaStoreChangeReceiver.notifyMediaStoreChanged()
        }
    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val binder = ServiceBinder()
    private var musicPlayerHandlerThread: HandlerThread? = null
    private var playerHandler: Handler? = null
    private var mediaCategoryObserver: MediaStoreObserver? = null

    inner class ServiceBinder : Binder() {
        val service get() = this@MediaStoreChangeListenerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            getTracks().asFlow().collect {
                localTracks(it)
                playerUpdate(it)
            }
        }
        musicPlayerHandlerThread = HandlerThread("PlaybackHandler")
        musicPlayerHandlerThread?.start()
        playerHandler = Handler(musicPlayerHandlerThread!!.looper)

        mediaCategoryObserver = MediaStoreObserver(mediaStoreChange, playerHandler!!)
        registerMediaStoreObserver(
            contentResolver = contentResolver,
            observer = mediaCategoryObserver!!
        )
    }

    override fun onDestroy() {
        unregisterMediaStoreObserver(mediaCategoryObserver!!)
        scope.cancel()
    }
}

fun Context.bindMediaStoreChangeListenerService(connection: ServiceConnection){
    val intent = Intent(this,MediaStoreChangeListenerService::class.java)
    bindService(intent,connection,Context.BIND_AUTO_CREATE)
}




