package com.gamapp.domain.usecase.player

interface ShuffleUseCase {
    suspend operator fun invoke()

    suspend operator fun invoke(enable: Boolean)
}