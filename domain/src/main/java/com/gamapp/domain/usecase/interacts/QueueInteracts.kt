package com.gamapp.domain.usecase.interacts

import com.gamapp.domain.usecase.data.queue.*
import javax.inject.Inject

class QueueInteracts @Inject constructor(
    val create: CreateQueueUseCase,
    val update: UpdateQueueUseCase,
    val getQueues: GetAllQueueUseCase,
    val removeQueue: RemoveQueueUseCase,
    val clear: ClearQueueUseCase,
    val removeTrackFromQueue: RemoveTrackFromQueueUseCase,
    val getTracks: GetTracksOfQueueUseCase,
    val addTrack: AddMusicsToQueueUseCase
)