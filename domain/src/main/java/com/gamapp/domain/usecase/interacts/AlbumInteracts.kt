package com.gamapp.domain.usecase.interacts

import com.gamapp.domain.usecase.data.album.AlbumSortOrderUseCase
import com.gamapp.domain.usecase.data.album.GetAlbumUseCase
import com.gamapp.domain.usecase.data.album.RemoveAlbumUseCase
import javax.inject.Inject


class AlbumInteracts @Inject constructor(
    val getAll: GetAlbumUseCase,
    val remove: RemoveAlbumUseCase,
    val sortOrder: AlbumSortOrderUseCase
)