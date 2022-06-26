package com.gamapp.data.mapper

import androidx.lifecycle.LiveData
import com.gamapp.data.entity.QueueEntity
import com.gamapp.domain.models.QueueModel

fun QueueEntity.toQueueModel(
    count: LiveData<Int>,
    imageId: LiveData<Long?>
): QueueModel {
    return QueueModel(
        id = this.id,
        title = this.title,
        count = count,
        imageIdLive = imageId,
        default = this.defaultQueue == 1
    )
}

fun QueueModel.toQueueEntity(): QueueEntity {
    return QueueEntity(
        id = id,
        title = title,
        defaultQueue = if (default) 1 else 0,
        count = count.value ?: 0,
        imageId = imageId
    )
}