package com.gamapp.data.di

import com.gamapp.data.repository.*
import com.gamapp.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSearchRepository(searchRepositoryImpl: SearchRepositoryImpl)
            : SearchRepository

    @Singleton
    @Binds
    abstract fun bindQueueRepository(
        queueRepositoryImp: QueueRepositoryImp
    ): QueueRepository


    @Singleton
    @Binds
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImp: FavoriteRepositoryImp
    ): FavoriteRepository


    @Binds
    @Singleton
    abstract fun provideTrackRepository(trackRepositoryImpl: TrackRepositoryImpl): TrackRepository

    @Binds
    @Singleton
    abstract fun provideAlbumRepository(albumRepository: AlbumRepositoryImpl): AlbumRepository

    @Binds
    @Singleton
    abstract fun provideArtistRepository(artistRepository: ArtistRepositoryImpl): ArtistRepository
}