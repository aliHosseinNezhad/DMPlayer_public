package com.gamapp.domain.usecase.interacts

import com.gamapp.domain.usecase.data.queue.GetTracksOfQueueUseCase
import com.gamapp.domain.usecase.data.queue.favorites.GetFavoriteTracksUseCase
import com.gamapp.domain.usecase.data.tracks.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackInteracts @Inject constructor(
    val get: GetAllTracksUseCase,
    val remove: RemoveTrackUseCase,
    val sortOrder: GetTracksSortOrderUseCase,
    val update: UpdateTrackUseCase,
    val getByAlbum: GetTracksByAlbumUseCase,
    val getByArtis: GetTracksByArtistUseCase,
    val getByQueue: GetTracksOfQueueUseCase,
    val getByFavorite: GetFavoriteTracksUseCase,
)