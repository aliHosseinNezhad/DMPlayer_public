package com.gamapp.domain.usecase.framework

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import javax.inject.Inject

class OpenApplicationDetailsUseCase @Inject constructor(private val application: Application) {
    fun invoke() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + application.packageName)
        intent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
            application.startActivity(intent)
    }
}