package com.gamapp.dmplayer.framework

import android.app.Activity
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistryOwner
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class ActivityResultRegisterProvider @Inject constructor() : LifecycleEventObserver {
    var activityResultRegistry: ActivityResultRegistry? = null
    fun <T> setup(owner: T) where T : LifecycleOwner, T : ActivityResultRegistryOwner {
        activityResultRegistry = owner.activityResultRegistry
        owner.lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event.targetState) {
            Lifecycle.State.DESTROYED -> {
                activityResultRegistry = null
            }
            else -> {

            }
        }
    }
}