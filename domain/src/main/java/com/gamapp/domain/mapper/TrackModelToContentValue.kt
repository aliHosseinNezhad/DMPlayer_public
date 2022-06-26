package com.gamapp.domain.mapper

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.gamapp.domain.models.TrackModel


fun TrackModel.toContentValue(): ContentValues {
    val value = ContentValues()
    value.put(MediaStore.Audio.AudioColumns.TITLE, title)
    value.put(MediaStore.Audio.AudioColumns.ARTIST, artist)
    value.put(MediaStore.Audio.AudioColumns.ALBUM, album)
    return value
}

@RequiresApi(Build.VERSION_CODES.R)
fun Context.update(trackModel: TrackModel) {
    val id = trackModel.id
    val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    contentResolver.update(uri, trackModel.toContentValue(), null, null)

//    MediaStore.createWriteRequest()
}