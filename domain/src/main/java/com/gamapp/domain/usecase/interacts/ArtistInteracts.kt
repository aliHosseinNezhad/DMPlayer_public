package com.gamapp.domain.usecase.interacts

import com.gamapp.domain.usecase.data.artist.ArtistSortOrderUseCase
import com.gamapp.domain.usecase.data.artist.GetArtistUseCase
import com.gamapp.domain.usecase.data.artist.RemoveArtistUseCase
import javax.inject.Inject

class ArtistInteracts @Inject constructor(
    val getArtists: GetArtistUseCase,
    val remove: RemoveArtistUseCase,
    val sortOrder: ArtistSortOrderUseCase
)