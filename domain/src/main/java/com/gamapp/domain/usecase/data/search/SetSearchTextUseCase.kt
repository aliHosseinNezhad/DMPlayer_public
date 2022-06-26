package com.gamapp.domain.usecase.data.search

import com.gamapp.domain.repository.SearchRepository
import javax.inject.Inject

class SetSearchTextUseCase @Inject constructor(private val repository: SearchRepository) {

    fun invoke(title: String) {
        repository.setTitle(title)
    }

}