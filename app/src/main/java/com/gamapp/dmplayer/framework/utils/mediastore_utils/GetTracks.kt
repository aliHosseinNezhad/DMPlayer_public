package com.gamapp.dmplayer.framework.utils.mediastore_utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.gamapp.domain.models.TrackModel

val collection: Uri
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }


@SuppressLint("Recycle")
suspend fun Context.getTracks(): List<TrackModel> {
    val query = contentResolver.query(
        collection,
        projection,
        null,
        null,
        null
    )
    return query.captureTracks()
}

@SuppressLint("Recycle")
suspend fun Context.getTracksByIds(ids: List<Long>): List<TrackModel> {
    val selection = ids.map {
        "${MediaStore.Audio.Media._ID} = $it"
    }.joinToString(separator = " OR ") { it }
    val query = contentResolver.query(
        collection,
        projection,
        selection,
        null,
        null
    )
    return query.captureTracks()
}