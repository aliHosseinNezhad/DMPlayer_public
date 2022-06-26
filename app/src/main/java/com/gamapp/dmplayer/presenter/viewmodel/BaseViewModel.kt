package com.gamapp.dmplayer.presenter.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import com.gamapp.dmplayer.presenter.utils.navigateBarHeight
import com.gamapp.dmplayer.presenter.utils.statusBarHeight

open class BaseViewModel(
    val application: Application,
) : ViewModel() {
    val context: Context get() = application.applicationContext
}