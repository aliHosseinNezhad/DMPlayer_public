package com.gamapp.data.entity

import androidx.room.*
import androidx.room.ForeignKey.*
import com.gamapp.data.constants.RoomConstants.queueId
import com.gamapp.data.constants.RoomConstants.trackId

@Entity(
    primaryKeys = [trackId, queueId],
    foreignKeys = [
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = [trackId],
            childColumns = [trackId],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = QueueEntity::class,
            parentColumns = [queueId],
            childColumns = [queueId],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],
    indices = [Index(queueId), Index(trackId)]
)
data class QueueTrackCrossRef(
    val queueId: String,
    val trackId: Long,
)
