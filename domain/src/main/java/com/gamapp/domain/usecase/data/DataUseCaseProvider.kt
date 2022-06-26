package com.gamapp.domain.usecase.data

import com.gamapp.domain.usecase.data.queue.AddMusicsToQueueUseCase
import com.gamapp.domain.usecase.interacts.QueueInteracts
import com.gamapp.domain.usecase.data.tracks.RemoveTrackUseCase
import javax.inject.Inject

class DataUseCaseProvider @Inject constructor(
    val queueInteracts: QueueInteracts,
    val addMusicsToQueueUseCase: AddMusicsToQueueUseCase,
    val removeTrackUseCase: RemoveTrackUseCase
)