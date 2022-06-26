package com.gamapp.domain.usecase.interacts

import javax.inject.Inject

class Interacts @Inject constructor(
    val album: AlbumInteracts,
    val artist: ArtistInteracts,
    val favorite: FavoriteInteracts,
    val player: PlayerInteracts,
    val queue: QueueInteracts,
    val search: SearchInteracts,
    val track: TrackInteracts
)