package com.gamapp.dmplayer.framework.di

import com.gamapp.dmplayer.framework.MediaStoreChangeHandlerImpl
import com.gamapp.dmplayer.framework.player.*
import com.gamapp.domain.mediaStore.MediaStoreChangeHandler
import com.gamapp.domain.mediaStore.MediaStoreChangeNotifier
import com.gamapp.domain.player_interface.PlayerData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun providePlayerController(controller:PlayerControllerImpl): PlayerController {
        return controller
    }

    @Singleton
    @Provides
    fun providePlayerConnection(connection: PlayerConnectionImpl): PlayerConnection {
        return connection
    }

    @Singleton
    @Provides
    fun providePlayerData(data: PlayerDataImpl): PlayerData {
        return data
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