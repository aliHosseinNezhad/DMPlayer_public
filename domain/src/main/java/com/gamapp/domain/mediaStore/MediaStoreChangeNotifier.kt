package com.gamapp.domain.mediaStore

interface MediaStoreChangeNotifier {
    fun register(mediaStoreChangeListener: MediaStoreChangeListener)
    fun unregister(mediaStoreChangeListener: MediaStoreChangeListener)
}