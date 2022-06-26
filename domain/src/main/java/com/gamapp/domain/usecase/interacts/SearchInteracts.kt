package com.gamapp.domain.usecase.interacts

import com.gamapp.domain.usecase.data.search.*
import javax.inject.Inject

class SearchInteracts @Inject constructor(
    val albums: GetAlbumsBySearchUseCase,
    val artist: GetArtistsBySearchUseCase,
    val tracks: GetTracksBySearchUseCase,
    val albumOrder: AlbumSearchSortOrderUseCase,
    val artistOrder: ArtistSearchSortOrderUseCase,
    val trackOrder: TrackSearchSortOrderUseCase,
    val setText: SetSearchTextUseCase,
    val getText: GetSearchTextUseCase,
)