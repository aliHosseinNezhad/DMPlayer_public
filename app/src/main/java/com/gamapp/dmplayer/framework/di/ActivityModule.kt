package com.gamapp.dmplayer.framework.di

import android.content.Context

import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {
    @Provides
    fun provideRegisterOwner(@ActivityContext context:Context): ActivityResultRegistry? {
        val result = context as? ActivityResultRegistryOwner
        return result?.activityResultRegistry
    }
}