package com.gamapp.domain.usecase.data.tracks

import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.TrackRepository
import com.gamapp.domain.sealedclasses.Sort
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class GetTracksSortOrderUseCase @Inject constructor(private val trackRepository: TrackRepository) {
    fun invoke(): MutableStateFlow<Sort<TrackModel>> {
        return trackRepository.trackSortOrder
    }
}