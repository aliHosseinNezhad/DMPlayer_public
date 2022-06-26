package com.gamapp.data.dao

import androidx.room.*
import com.gamapp.data.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trackEntity: TrackEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(tracks: TrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tracks: List<TrackEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(tracks: List<TrackEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(track: TrackEntity)


    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(tracks: List<TrackEntity>)


    @Delete
    suspend fun delete(trackEntity: TrackEntity)

    @Delete
    suspend fun delete(tracks: List<TrackEntity>)

    @Query("select * from TrackEntity")
    fun getAllViaFlow(): Flow<List<TrackEntity>>

    @Query("select * from TrackEntity")
    suspend fun getAll(): List<TrackEntity>
}