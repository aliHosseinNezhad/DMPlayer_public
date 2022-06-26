package com.gamapp.data.repository

import com.gamapp.data.data_source.media_store.MediaStoreFetchDataSource
import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.repository.AlbumRepository
import com.gamapp.domain.sealedclasses.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

suspend fun <T> Flow<T>.collect(collect: (T) -> Unit) {
    collect { value -> collect(value) }
}

class AlbumRepositoryImpl @Inject constructor(
    private val mediaStoreFetchDataSource: MediaStoreFetchDataSource,
) : AlbumRepository {
    override fun albums(): Flow<List<AlbumModel>> {
        return mediaStoreFetchDataSource.getAllAlbums().combine(albumSortOrder, transform = { a, b ->
            b.method(a)
        })
    }


    override val albumSortOrder: MutableStateFlow<Sort<AlbumModel>> =
        MutableStateFlow(AlbumSort(AlbumSort.SortByDataAdded, order = Order.ETS))
}