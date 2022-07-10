package com.gamapp.dmplayer.framework.di

import com.gamapp.data.data_source.media_store.MediaStoreFetchDataSource
import com.gamapp.data.data_source.media_store.MediaStoreUpdateTrackDataSource
import com.gamapp.data.data_source.media_store.remove.MediaStoreRemoveTracksDataSource
import com.gamapp.dmplayer.framework.data_source.MediaStoreFetchDataSourceImpl
import com.gamapp.dmplayer.framework.data_source.remove.MediaStoreRemoveTracksDataSourceImpl
import com.gamapp.dmplayer.framework.data_source.update.MediaStoreUpdateTrackDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MediaStoreDataSourceModule {
    @Singleton
    @Provides
    fun provideFetchData(source: MediaStoreFetchDataSourceImpl): MediaStoreFetchDataSource {
        return source
    }
}

@Module
@InstallIn(SingletonComponent::class)
object MediaStoreActivityRetainedModule {

    @Singleton
    @Provides
    fun provideRemoveTracks(source: MediaStoreRemoveTracksDataSourceImpl): MediaStoreRemoveTracksDataSource {
        return source
    }

    @Singleton
    @Provides
    fun provideUpdateTracks(source: MediaStoreUpdateTrackDataSourceImpl): MediaStoreUpdateTrackDataSource = source
}