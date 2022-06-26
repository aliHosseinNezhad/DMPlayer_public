package com.gamapp.data.entity

import androidx.annotation.NonNull
import androidx.room.*
import com.gamapp.data.constants.RoomConstants.queueId

@Entity
data class QueueEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(index = true, name = queueId)
    @NonNull
    val id: String,
    val title:String,
    val defaultQueue:Int = 0,
    val count:Int = 0,
    val imageId:Long? = null
)