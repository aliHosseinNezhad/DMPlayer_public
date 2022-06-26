package com.gamapp.data.repository

import androidx.lifecycle.asLiveData
import com.gamapp.data.data_source.media_store.MediaStoreFetchDataSource
import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.SearchRepository
import com.gamapp.domain.sealedclasses.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject


class SearchRepositoryImpl @Inject constructor(
    private val source: MediaStoreFetchDataSource
) : SearchRepository {

    private val searchTitle = MutableStateFlow("")

    override fun setTitle(title: String) = searchTitle.tryEmit(title)

    override fun getTitle() = searchTitle.asLiveData()

    override val albumSortOrder: MutableStateFlow<Sort<AlbumModel>> =
        MutableStateFlow(AlbumSort(AlbumSort.SortByDataAdded, order = Order.ETS))
    override val artistSortOrder: MutableStateFlow<Sort<ArtistModel>> =
        MutableStateFlow(ArtistSort(type = ArtistSort.SortByDataAdded, order = Order.ETS))
    override val tracksSortOrder: MutableStateFlow<Sort<TrackModel>> =
        MutableStateFlow(TrackSort(TrackSort.SortByDateAdded, order = Order.ETS))

    override fun albums(): Flow<List<AlbumModel>> {
        return source.getAllAlbums().combine(searchTitle, transform = { a, b ->
            if (b.isNotBlank()) {
                a.filter {
                    it.title.lowercase()
                        .contains(b.lowercase())
                }
            } else emptyList()
        }).combine(albumSortOrder, transform = { a, b ->
            b.method(a)
        })
    }

    override fun artists(): Flow<List<ArtistModel>> {
        return source.getAllArtists().combine(searchTitle, transform = { a, b ->
            if (b.isNotBlank()) {
                a.filter {
                    it.title.lowercase()
                        .contains(b.lowercase())
                }
            } else emptyList()
        }).combine(artistSortOrder, transform = { a, b ->
            b.method(a)
        })
    }

    override fun tracks(): Flow<List<TrackModel>> {
        return source.getAllTracks().combine(searchTitle, transform = { a, b ->
            if (b.isNotBlank()) {
                a.filter {
                    it.title.lowercase()
                        .contains(b.lowercase())
                }
            } else emptyList()
        }).combine(tracksSortOrder, transform = { a, b ->
            b.method(a)
        })
    }
}