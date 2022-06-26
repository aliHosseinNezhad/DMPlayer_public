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


@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    @ViewModelScoped
    abstract fun bindSearchRepository(searchRepositoryImpl: SearchRepositoryImpl)
            : SearchRepository

    @ViewModelScoped
    @Binds
    abstract fun bindQueueRepository(
        queueRepositoryImp: QueueRepositoryImp
    ): QueueRepository


    @ViewModelScoped
    @Binds
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImp: FavoriteRepositoryImp
    ): FavoriteRepository


    @Binds
    @ViewModelScoped
    abstract fun provideTrackRepository(trackRepositoryImpl: TrackRepositoryImpl): TrackRepository

    @Binds
    @ViewModelScoped
    abstract fun provideAlbumRepository(albumRepository: AlbumRepositoryImpl): AlbumRepository

    @Binds
    @ViewModelScoped
    abstract fun provideArtistRepository(artistRepository: ArtistRepositoryImpl): ArtistRepository
}