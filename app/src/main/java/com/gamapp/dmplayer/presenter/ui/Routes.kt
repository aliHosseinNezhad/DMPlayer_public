package com.gamapp.dmplayer.presenter.ui


sealed class Routes(val name: String ) {
    object App: Routes("App")
    object Permission: Routes("Permission")
    object Albums: Routes("Albums")
    object MusicList: Routes("MusicList")
    object TracksByQueue:Routes("TracksByQueue")
    object TracksByFavorite:Routes("TracksByFavorite")
    object TracksByAlbum:Routes("TracksByAlbum")
    object TracksByArtis:Routes("TracksByArtis")
}