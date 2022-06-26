package com.gamapp.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.gamapp.data.constants.RoomConstants.queueId
import com.gamapp.data.constants.RoomConstants.trackId

data class QueueWithTracks(
    @Embedded val queue: QueueEntity,
    @Relation(
        parentColumn = queueId,
        entityColumn = trackId,
        associateBy = Junction(QueueTrackCrossRef::class,
            parentColumn = queueId,
            entityColumn = trackId)
    )
    val tracks: List<TrackEntity>,
)

data class TrackWithQueues(
    @Embedded val track: TrackEntity,
    @Relation(
        parentColumn = trackId,
        entityColumn = queueId,
        associateBy = Junction(QueueTrackCrossRef::class,
            parentColumn = trackId,
            entityColumn = queueId)
    )
    val queues: List<QueueEntity>,
)