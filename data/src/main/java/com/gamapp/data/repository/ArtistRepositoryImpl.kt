package com.gamapp.data.repository

import com.gamapp.data.data_source.media_store.MediaStoreFetchDataSource
import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.repository.ArtistRepository
import com.gamapp.domain.sealedclasses.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    private val mediaStoreFetchDataSource: MediaStoreFetchDataSource,
) : ArtistRepository {
    override val artistSortOrder: MutableStateFlow<Sort<ArtistModel>> =
        MutableStateFlow(ArtistSort(ArtistSort.SortByDataAdded, order = Order.ETS))

    override fun artists(): Flow<List<ArtistModel>> {
        return mediaStoreFetchDataSource.getAllArtists()
            .combine(artistSortOrder, transform = { a, b ->
                b.method(a)
            })
    }
}