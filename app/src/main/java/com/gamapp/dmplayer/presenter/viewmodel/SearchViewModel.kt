package com.gamapp.dmplayer.presenter.viewmodel

import androidx.lifecycle.ViewModel
import com.gamapp.dmplayer.presenter.ui.screen.menu.AlbumMenu
import com.gamapp.dmplayer.presenter.ui.screen.menu.ArtistMenu
import com.gamapp.dmplayer.presenter.ui.screen.menu.tracks.TracksMenu
import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.usecase.interacts.SearchInteracts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    val searchInteracts: SearchInteracts,
    val tracksMenu: TracksMenu,
    val albumMenu: AlbumMenu,
    val artistMenu: ArtistMenu
) : ViewModel() {
    fun albums(): Flow<List<AlbumModel>> {
        return searchInteracts.albums.invoke()
    }
    fun artists(): Flow<List<ArtistModel>> {
        return searchInteracts.artist.invoke()
    }
    fun tracks(): Flow<List<TrackModel>> {
        return searchInteracts.tracks.invoke()
    }
    fun setTitle(title:String){
        searchInteracts.setText.invoke(title)
    }
}