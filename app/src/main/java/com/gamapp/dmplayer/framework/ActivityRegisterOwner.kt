package com.gamapp.dmplayer.framework

import androidx.activity.result.ActivityResultRegistry
import com.gamapp.domain.ActivityRegisterResultProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ActivityRegisterProviderModule {
    @Singleton
    @Provides
    fun provideRegisterResult(result: ActivityRegisterResultResultProviderImpl): ActivityRegisterResultProvider {
        return result
    }
}

@Singleton
class ActivityRegisterResultResultProviderImpl @Inject constructor() : ActivityRegisterResultProvider {
    override var activityRegister: ActivityResultRegistry? = null
    fun onCreate(register: ActivityResultRegistry) {
        activityRegister = register
    }

    fun onDestroy() {
        activityRegister = null
    }
}