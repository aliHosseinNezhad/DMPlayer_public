package com.gamapp.domain.models

import android.os.Parcelable
import com.gamapp.graphics.R
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArtistModel(
    override val id: Long,
    override val tracks: List<TrackModel>
) : CategoryModel, LongId ,Parcelable {
    @IgnoredOnParcel
    val album = tracks.safe.album
    @IgnoredOnParcel
    val count = tracks.size
    @IgnoredOnParcel
    val dateAdded: Long by lazy {
        tracks.maxOfOrNull { it.dateAdded }
            ?: TrackModel.empty.dateAdded
    }
    @IgnoredOnParcel
    val albums by lazy {
        tracks.groupBy { it.albumId }
    }

    @IgnoredOnParcel
    override val title = tracks.safe.artist
    @IgnoredOnParcel
    override val imageId = tracks.safe.id
    @IgnoredOnParcel
    override val subtitle: String =
        "${albums.size} albums | $count track${count.makeS()}"
    @IgnoredOnParcel
    override val defaultImage: Int = R.drawable.ic_artist

    companion object {
        val empty = ArtistModel(-1, emptyList())
    }
}

val ArtistModel?.safe get() = this ?: ArtistModel.empty
