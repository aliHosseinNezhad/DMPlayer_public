package com.gamapp.dmplayer.framework.di

import android.content.Context
import com.gamapp.dmplayer.framework.MediaStoreChangeHandlerImpl
import com.gamapp.dmplayer.framework.player.*
import com.gamapp.domain.mediaStore.MediaStoreChangeHandler
import com.gamapp.domain.mediaStore.MediaStoreChangeNotifier
import com.gamapp.domain.player_interface.PlayerConnection
import com.gamapp.domain.player_interface.PlayerController
import com.gamapp.domain.player_interface.PlayerEvents
import com.google.android.exoplayer2.upstream.DefaultDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    //    @Singleton
//    @Provides
//    fun providePlayWithTimer(playWithTimer: com.gamapp.dmplayer.framework.player.PlayWithTimerImpl): com.gamapp.dmplayer.framework.player.PlayWithTimer {
//        return playWithTimer
//    }
    @Singleton
    @Provides
    fun provideDataSourceFactory(@ApplicationContext context: Context): DefaultDataSource.Factory {
        return DefaultDataSource.Factory(context)
    }

    @Singleton
    @Provides
    fun providePlayerConnection(connection: PlayerConnectionImpl): PlayerConnection {
        return connection
    }

    @Singleton
    @Provides
    fun providePlayerController(connection: PlayerConnection): PlayerController {
        return connection.controllers
    }

    @Singleton
    @Provides
    fun providePlayerEvents(connection: PlayerConnection): PlayerEvents {
        return connection.playerEvents
    }

    @Singleton
    @Provides
    fun provideMediaStoreChangeHandler(mediaStoreChangeHandler: MediaStoreChangeHandlerImpl): MediaStoreChangeHandler {
        return mediaStoreChangeHandler
    }

    @Singleton
    @Provides
    fun provideMediaStoreChangeNotifier(notifier: MediaStoreChangeHandlerImpl): MediaStoreChangeNotifier {
        return notifier
    }
}