package com.gamapp.data.di

import com.gamapp.data.db.ApplicationDatastore
import com.gamapp.data.db.ApplicationDatastoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDataStore(dataStore: ApplicationDatastoreImpl): ApplicationDatastore {
        return dataStore
    }
}