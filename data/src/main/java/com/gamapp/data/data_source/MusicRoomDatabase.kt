package com.gamapp.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gamapp.data.entity.TrackEntity
import com.gamapp.data.dao.MusicDao
import com.gamapp.data.dao.QueueDao
import com.gamapp.data.entity.QueueEntity
import com.gamapp.data.entity.QueueTrackCrossRef

@Database(entities = [
    TrackEntity::class,
    QueueEntity::class,
    QueueTrackCrossRef::class],
    version = 2,
    exportSchema = false)
abstract class MusicRoomDatabase : RoomDatabase() {
    abstract val musicDao: MusicDao
    abstract val queueDao: QueueDao
}