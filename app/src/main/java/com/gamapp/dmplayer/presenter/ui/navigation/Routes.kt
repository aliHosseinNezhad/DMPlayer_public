package com.gamapp.dmplayer.presenter.ui.navigation

import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.navigation.nav_ext.Route
import com.gamapp.domain.models.TrackModel
import kotlinx.parcelize.Parcelize


@Parcelize
class MainRoute(val title: String = "Category") : Route()


@Parcelize
data class TrackByAlbumRoute(
    val album: Long
) : Route()

@Parcelize
data class TrackByArtistRoute(
    val artist: Long
) : Route()

@Parcelize
data class TrackByFavoriteRoute(
    val title: Int = R.string.favorite
) : Route()

@Parcelize
data class TrackByQueueRoute(
    val id: String
) : Route()


@Parcelize
data class TracksBySearchRoute(
    val text: String
) : Route()

@Parcelize
data class AlbumsBySearchRoute(
    val text: String
) : Route()

@Parcelize
data class ArtistsBySearchRoute(
    val text:String
) : Route()


@Parcelize
data class TrackDetailsRoute(
    val track:TrackModel
) : Route()
