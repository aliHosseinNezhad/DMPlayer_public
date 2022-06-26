package com.gamapp.domain.usecase.interacts

import com.gamapp.domain.usecase.data.queue.favorites.*
import javax.inject.Inject

class FavoriteInteracts @Inject constructor(
    val isFavorite: TrackIsFavoriteUseCase,
    val getAllFavorites: GetFavoriteTracksUseCase,
    val remove: RemoveTrackFromFavoriteUseCase,
    val clear: ClearFavoriteUseCase,
    val addTo: AddToFavoriteUseCase
)