package com.gamapp.domain.usecase.interacts

import com.gamapp.domain.usecase.data.queue.AddMusicsToQueueUseCase
import com.gamapp.domain.usecase.data.queue.favorites.AddToFavoriteUseCase
import com.gamapp.domain.usecase.player.*
import javax.inject.Inject

class PlayerInteracts @Inject constructor(
    val playPause: PlayPauseUseCase,
    val skipToNext: SkipToNextUseCase,
    val skipToPrevious: SkipToPreviousUseCase,
    val forward: ForwardUseCase,
    val rewind: RewindUseCase,
    val repeatModeUseCase: RepeatModeUseCase,
    val seekBarUseCase: SeekBarUseCase,
    val shuffleUseCase: ShuffleUseCase,
    val setPlayListAndPlay: SetPlayListAndPlayUseCase,
    val setPlayList: SetPlayListUseCase,
    val setCurrentTrack:SetCurrentTrackUseCase,
    val addToFavoriteUseCase: AddToFavoriteUseCase,
    val addMusicsToQueueUseCase: AddMusicsToQueueUseCase
)