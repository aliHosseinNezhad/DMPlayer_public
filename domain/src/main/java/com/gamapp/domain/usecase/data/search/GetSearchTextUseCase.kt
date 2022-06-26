package com.gamapp.domain.usecase.data.search

import androidx.lifecycle.LiveData
import com.gamapp.domain.repository.SearchRepository
import javax.inject.Inject

class GetSearchTextUseCase @Inject constructor(private val repository: SearchRepository) {
    fun invoke(): LiveData<String> {
        return repository.getTitle()
    }
}