package com.gamapp.dmplayer.framework.player.usecase

import com.gamapp.domain.usecase.data.queue.AddMusicsToQueueUseCase
import com.gamapp.domain.usecase.data.queue.favorites.AddToFavoriteUseCase
import com.gamapp.domain.usecase.interacts.PlayerInteracts
import com.gamapp.domain.usecase.player.*
import javax.inject.Inject

class PlayerInteractsImpl @Inject constructor(
    override val playPauseUseCase: PlayPauseUseCase,
    override val fastForwardUseCase: FastForwardUseCase,
    override val fastRewindUseCase: FastRewindUseCase,
    override val repeatModeUseCase: RepeatModeUseCase,
    override val seekBarUseCase: SeekBarUseCase,
    override val shuffleUseCase: ShuffleUseCase,
    override val addToFavoriteUseCase: AddToFavoriteUseCase,
    override val addMusicsToQueueUseCase: AddMusicsToQueueUseCase
) :PlayerInteracts