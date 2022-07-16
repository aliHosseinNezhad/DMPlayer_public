package com.gamapp.dmplayer.presenter.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gamapp.dmplayer.presenter.ui.navigation.nav_ext.Route
import com.gamapp.dmplayer.presenter.ui.navigation.nav_ext.composable
import com.gamapp.dmplayer.presenter.ui.screen.category.MainCategoryPage
import com.gamapp.dmplayer.presenter.ui.screen.listof.artist.ArtistsBySearch
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.albumList.AlbumsBySearch
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.*
import com.gamapp.dmplayer.presenter.ui.screen.track_details.TrackDetails
import kotlinx.parcelize.Parcelize

@Composable
fun SetUpNavigation(nav: NavHostController) {
    val builder = remember<NavGraphBuilder.() -> Unit> {
        {
            composable(route = "main") {
                MainCategoryPage(
                    trackViewModel = hiltViewModel(),
                    categoryViewModel = hiltViewModel(),
                    queueViewModel = hiltViewModel(),
                    nav = nav
                )
            }
            composable<TrackByAlbumRoute> {
                TracksByAlbum(albumId = it.album, viewModel = hiltViewModel(), nav = nav)
            }
            composable<TrackByArtistRoute> {
                TracksByArtist(artistId = it.artist, viewModel = hiltViewModel(), nav = nav)
            }
            composable<TrackByFavoriteRoute> {
                TracksByFavorite(viewModel = hiltViewModel(), nav = nav)
            }
            composable<TrackByQueueRoute> {
                TracksByQueue(queueId = it.id, nav = nav)
            }
            composable<TrackDetailsRoute> {
                TrackDetails(track = it.track, nav = nav)
            }
            search(nav = nav)
        }
    }
    NavHost(navController = nav, startDestination = "main", builder = builder)
}


fun NavGraphBuilder.search(nav: NavHostController) {
    composable<TracksBySearchRoute> {
        TracksBySearch(title = it.text, nav = nav)
    }
    composable<AlbumsBySearchRoute> {
        AlbumsBySearch(text = it.text, nav = nav)
    }
    composable<ArtistsBySearchRoute> {
        ArtistsBySearch(text = it.text, nav = nav)
    }
}