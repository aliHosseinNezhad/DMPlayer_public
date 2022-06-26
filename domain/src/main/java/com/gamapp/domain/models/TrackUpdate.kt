package com.gamapp.domain.models

import android.content.ContentValues
import android.provider.MediaStore

sealed class TrackUpdate(val key: String) {
    abstract val value: Any?
    class Title(
        override val value: String?
    ) : TrackUpdate(key = MediaStore.Audio.AudioColumns.TITLE)

    class Artist(
        override val value: String?
    ) : TrackUpdate(key = MediaStore.Audio.AudioColumns.ARTIST)

    class Album(
        override val value: String?
    ) : TrackUpdate(key = MediaStore.Audio.AudioColumns.ALBUM)

    class DisplayName(
        override val value: String?
    ) : TrackUpdate(key = MediaStore.Audio.AudioColumns.DISPLAY_NAME)
}

fun TrackUpdate.toContentValue(content: ContentValues): TrackUpdate {
    when (this) {
        is TrackUpdate.Title -> {
            content.put(key, value)
        }
        is TrackUpdate.Album -> {
            content.put(key, value)
        }
        is TrackUpdate.Artist -> {
            content.put(key, value)
        }
        is TrackUpdate.DisplayName -> {
            content.put(key, value)
        }
    }
    return this
}

fun List<TrackUpdate>.toContentValue(): ContentValues {
    val contentValues = ContentValues()
    forEach {
        it.toContentValue(contentValues)
    }
    return contentValues
}

