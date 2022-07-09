package com.gamapp.domain.usecase.player


import com.gamapp.domain.player_interface.PlayerData
import kotlinx.coroutines.flow.take
import javax.inject.Inject


interface SeekBarUseCase  {
    suspend operator fun invoke(progress: Float)
}
