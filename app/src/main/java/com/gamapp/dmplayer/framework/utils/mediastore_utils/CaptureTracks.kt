package com.gamapp.dmplayer.framework.utils.mediastore_utils

import android.database.Cursor
import android.provider.MediaStore
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.gamapp.domain.models.TrackModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


val projection = arrayOf(
    MediaStore.Audio.AudioColumns._ID,
    MediaStore.Audio.AudioColumns.DISPLAY_NAME,
    MediaStore.Audio.AudioColumns.DURATION,
    MediaStore.Audio.AudioColumns.SIZE,
    MediaStore.Audio.AudioColumns.TITLE,
    MediaStore.Audio.AudioColumns.ARTIST,
    MediaStore.Audio.AudioColumns.ALBUM,
    MediaStore.Audio.AudioColumns.ARTIST_ID,
    MediaStore.Audio.AudioColumns.ALBUM_ID,
    MediaStore.Audio.AudioColumns.DATE_ADDED,
    MediaStore.Audio.AudioColumns.COMPOSER,
    MediaStore.Audio.AudioColumns.IS_MUSIC,
    MediaStore.Audio.AudioColumns.IS_ALARM,
)

suspend fun Cursor?.captureTracks(): List<TrackModel> {
    this?.use { cursor ->
        val tracks = mutableListOf<TrackModel>()
        cursor.use {
            withContext(Dispatchers.IO) {
                try {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
                    val fileNameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
                    val durationColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
                    val sizeColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE)
                    val titleColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
                    val artistColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
                    val albumColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
                    val artistIdColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST_ID)
                    val albumIdColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
                    val dataAddedColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_ADDED)
                    val isMusicColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.IS_MUSIC)
                    while (moveToNext()) {
                        val id = cursor.getLongOrNull(idColumn) ?: continue
                        val fileName = cursor.getStringOrNull(fileNameColumn) ?: ""
                        val duration = cursor.getIntOrNull(durationColumn) ?: 0
                        val size = cursor.getIntOrNull(sizeColumn) ?: 0
                        val title = cursor.getStringOrNull(titleColumn) ?: ""
                        val artist = cursor.getStringOrNull(artistColumn) ?: ""
                        val album = cursor.getStringOrNull(albumColumn) ?: ""
                        val artistId = cursor.getLongOrNull(artistIdColumn) ?: -1
                        val albumId = cursor.getLongOrNull(albumIdColumn) ?: -1
                        val dateAdded = cursor.getLongOrNull(dataAddedColumn) ?: 0
                        val isMusic = cursor.getIntOrNull(isMusicColumn)?.let {
                            it == 1
                        } ?: true
                        if (!isMusic)
                            continue
                        tracks += TrackModel(
                            fileName = fileName,
                            id = id,
                            title = title,
                            artist = artist,
                            album = album,
                            duration = duration,
                            size = size,
                            albumId = albumId,
                            artistId = artistId,
                            dateAdded = dateAdded
                        )
                    }
                } catch (e: Exception) {
                }
            }
            return tracks
        }
    } ?: return emptyList()
}