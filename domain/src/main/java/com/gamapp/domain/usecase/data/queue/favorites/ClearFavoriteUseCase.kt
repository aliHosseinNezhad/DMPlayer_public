package com.gamapp.domain.usecase.data.queue.favorites

import com.gamapp.domain.repository.FavoriteRepository
import javax.inject.Inject

class ClearFavoriteUseCase @Inject constructor(private val repository: FavoriteRepository) {
    suspend fun invoke() {
        repository.clear()
    }
}