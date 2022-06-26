package com.gamapp.data.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.gamapp.data.constants.RoomConstants.trackId

@Entity
data class TrackEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = trackId, index = true)
    val fileId: Long,
    val fileName: String,
    val title: String,
    val artist: String,
    val duration: Int,
    val size: Int,
    val artistId: Long,
    val albumId: Long,
    val album: String,
    val dateAdded: Long
) {
    override fun toString() =
        "(id:$fileId,fileName:$fileName,title:$title,artist:$artist,album:$album)"
}
