package com.gamapp.dmplayer.framework.service

import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.provider.MediaStore
import com.gamapp.domain.ACTIONS


class MediaCategoryObserver(
    private val musicService: MusicService,
    private val mHandler: Handler,
) : ContentObserver(mHandler), Runnable {

    companion object {
        const val TAG = "MediaStoreObserverChang"

        // milliseconds to delay before calling refresh to aggregate events
        private const val REFRESH_DELAY: Long = 500
    }

    override fun onChange(selfChange: Boolean) {
        // if a change is detected, remove any scheduled callback
        // then post a new one. This is intended to prevent closely
        // spaced events from generating multiple refresh calls
        mHandler.removeCallbacks(this)
        mHandler.postDelayed(this, REFRESH_DELAY)
    }

    override fun run() {

        musicService.notifyChange(ACTIONS.MEDIA_STORE_CHANGED)
    }
}

fun registerCategoryObserver(
    contentResolver: ContentResolver,
    mediaCategoryObserver: MediaCategoryObserver,
) {
    contentResolver
        .registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mediaCategoryObserver
        )
    contentResolver
        .registerContentObserver(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, true, mediaCategoryObserver
        )
    contentResolver
        .registerContentObserver(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, true, mediaCategoryObserver
        )
    contentResolver
        .registerContentObserver(
            MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, true, mediaCategoryObserver
        )

}
