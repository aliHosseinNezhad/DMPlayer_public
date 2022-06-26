package com.gamapp.domain.usecase.data.search

import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.SearchRepository
import com.gamapp.domain.sealedclasses.Sort
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class TrackSearchSortOrderUseCase @Inject constructor(private val repository: SearchRepository) {
    fun invoke(): MutableStateFlow<Sort<TrackModel>> {
        return repository.tracksSortOrder
    }
}