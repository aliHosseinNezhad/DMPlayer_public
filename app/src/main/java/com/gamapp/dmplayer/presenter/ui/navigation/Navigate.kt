package com.gamapp.dmplayer.presenter.ui.navigation

import androidx.navigation.NavHostController
import com.gamapp.dmplayer.presenter.ui.navigation.nav_ext.navigateTo
import com.gamapp.domain.models.BaseTrack
import com.gamapp.domain.models.TrackModel


fun NavHostController.toAlbum(id: Long) {
    this.navigateTo(TrackByAlbumRoute(id))
}

fun NavHostController.toArtist(id: Long) {
    this.navigateTo(TrackByArtistRoute(id))
}

fun NavHostController.toQueue(id: String) {
    this.navigateTo(TrackByQueueRoute(id))
}

fun NavHostController.toFavorite() {
    this.navigateTo(TrackByFavoriteRoute())
}

fun NavHostController.toTracksBySearch(text: String) {
    this.navigateTo(TracksBySearchRoute(text))
}

fun NavHostController.toAlbumsBySearch(text: String) {
    this.navigateTo(AlbumsBySearchRoute(text))
}

fun NavHostController.toArtistsBySearch(text: String) {
    this.navigateTo(ArtistsBySearchRoute(text))
}

fun NavHostController.toTrackDetails(track: BaseTrack) {
    navigateTo(TrackDetailsRoute(track))
}