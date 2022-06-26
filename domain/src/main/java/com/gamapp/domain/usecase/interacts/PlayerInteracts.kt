package com.gamapp.domain.usecase.interacts

import com.gamapp.domain.usecase.data.queue.AddMusicsToQueueUseCase
import com.gamapp.domain.usecase.data.queue.favorites.AddToFavoriteUseCase
import com.gamapp.domain.usecase.player.*
import javax.inject.Inject

data class PlayerInteracts @Inject constructor(
    val playPauseUseCase: PlayPauseUseCase,
    val fastForwardUseCase: FastForwardUseCase,
    val fastRewindUseCase: FastRewindUseCase,
    val repeatModeUseCase: RepeatModeUseCase,
    val seekBarUseCase: SeekBarUseCase,
    val shuffleUseCase: ShuffleUseCase,
    val addToFavoriteUseCase: AddToFavoriteUseCase,
    val addMusicsToQueueUseCase: AddMusicsToQueueUseCase
)