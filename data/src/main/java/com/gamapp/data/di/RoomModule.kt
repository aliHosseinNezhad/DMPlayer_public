package com.gamapp.data.di

import android.content.Context
import androidx.room.Room
import com.gamapp.data.dao.MusicDao
import com.gamapp.data.dao.QueueDao
import com.gamapp.data.data_source.MusicRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RoomModule {
    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context): MusicRoomDatabase {
        return Room.databaseBuilder(
            context,
            MusicRoomDatabase::class.java,
            "music_room_database"
        ).createFromAsset("music_database").fallbackToDestructiveMigrationOnDowngrade().build()
    }

    @Singleton
    @Provides
    fun provideMusicDao(musicRoomDatabase: MusicRoomDatabase): MusicDao {
        return musicRoomDatabase.musicDao
    }

    @Singleton
    @Provides
    fun provideQueueDao(musicRoomDatabase: MusicRoomDatabase): QueueDao {
        return musicRoomDatabase.queueDao
    }
}