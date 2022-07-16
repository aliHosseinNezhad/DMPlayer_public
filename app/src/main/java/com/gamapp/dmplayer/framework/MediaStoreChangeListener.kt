package com.gamapp.dmplayer.framework

import com.gamapp.domain.mediaStore.MediaStoreChangeReceiver
import com.gamapp.domain.mediaStore.MediaStoreChangeListener
import com.gamapp.domain.mediaStore.MediaStoreChangeNotifier
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaStoreChangeReceiverImpl @Inject constructor() :
    MediaStoreChangeReceiver,
    MediaStoreChangeNotifier {
    private val observers: MutableList<MediaStoreChangeListener> = mutableListOf()

    override fun notifyMediaStoreChanged() {
        observers.forEach {
            it.onMediaStoreChanged()
        }
    }

    override fun register(mediaStoreChangeListener: MediaStoreChangeListener) {
        observers += mediaStoreChangeListener
    }

    override fun unregister(mediaStoreChangeListener: MediaStoreChangeListener) {
        observers -= mediaStoreChangeListener
    }
}
