package com.gamapp.domain.usecase.data.search

import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.repository.SearchRepository
import com.gamapp.domain.sealedclasses.Sort
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class AlbumSearchSortOrderUseCase @Inject constructor(private val searchRepository: SearchRepository) {
    fun invoke(): MutableStateFlow<Sort<AlbumModel>> {
        return searchRepository.albumSortOrder
    }
}