package com.gamapp.domain.usecase.player


interface SeekBarUseCase  {
    suspend operator fun invoke(progress: Float)
}
