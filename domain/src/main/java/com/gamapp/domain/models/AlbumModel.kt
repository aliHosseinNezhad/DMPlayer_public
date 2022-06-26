package com.gamapp.domain.models

import android.os.Parcelable
import com.gamapp.graphics.R
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlbumModel(
    override val id: Long,
    override val tracks: List<TrackModel>
) : CategoryModel,LongIdModel, Parcelable {
    @IgnoredOnParcel
    val count = tracks.size
    @IgnoredOnParcel
    val artist = tracks.safe.artist
    @IgnoredOnParcel
    override val imageId = tracks.safe.id
    @IgnoredOnParcel
    override val defaultImage: Int = R.drawable.round_album_24
    @IgnoredOnParcel
    val dateAdded: Long by lazy {
        tracks.maxOfOrNull { it.dateAdded } ?: TrackModel.empty.dateAdded
    }
    @IgnoredOnParcel
    override val title = tracks.safe.album
    @IgnoredOnParcel
    override val subtitle: String = "$artist | $count track${count.makeS()}"

    companion object {
        val empty = AlbumModel(
            -1,
            emptyList()
        )
    }
}

val AlbumModel?.safe get() = this ?: AlbumModel.empty
