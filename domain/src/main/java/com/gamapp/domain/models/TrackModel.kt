package com.gamapp.domain.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.gamapp.graphics.R
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

val TrackModel.Companion.Empty
    get() = TrackModel(
        id = -1,
        title = "",
        fileName = "",
        artist = "",
        album = "",
        artistId = -1,
        albumId = -1,
        dateAdded = Long.MIN_VALUE,
        size = 0,
        duration = 0
    )

@Keep
@Parcelize
data class TrackModel(
    override val fileName: String,
    override val id: Long,
    override val title: String,
    override val artist: String,
    override val album: String,
    override val dateAdded: Long,
    override val duration: Int,
    val size: Int,
    val albumId: Long,
    val artistId: Long,
) : BaseTrack, Parcelable {
    override val subtitle: String get() = artist
    override val defaultImage: Int get() = R.drawable.ic_track
    override val imageId: Long
        get() = id

    companion object {
        val empty = TrackModel(
            "",
            -1,
            title = "",
            artist = "",
            album = "",
            duration = 0,
            size = 0,
            albumId = -1,
            artistId = -1,
            dateAdded = 0,
        )
    }

    override fun hashCode(): Int {
        return arrayOf<Any>(
            fileName,
            id,
            title,
            artist,
            album,
            duration,
            size,
            albumId,
            artistId,
            dateAdded
        ).contentHashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TrackModel
        if (fileName != other.fileName) return false
        if (id != other.id) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        if (duration != other.duration) return false
        if (size != other.size) return false
        if (artistId != other.artistId) return false
        if (albumId != other.albumId) return false
        if (dateAdded != other.dateAdded) return false
        return true
    }
}


val List<TrackModel>.safe get() = if (isNotEmpty()) first() else TrackModel.empty

