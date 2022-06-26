package com.gamapp.dmplayer.presenter.models

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

data class TrackPlayModel(
    var title: String = "",
    var fileName: String = "",
    var artist: String = "",
    var id: Long? = null,
    var bitmap: ImageBitmap? = null,
    val isFavorite: LiveData<Boolean> = MutableLiveData(false)
)
fun emptyPlayModel(): TrackPlayModel {
    return TrackPlayModel(
        title = "",
        fileName = "",
        artist = "",
        id = null,
        bitmap = null,
        isFavorite = MutableLiveData(false)
    )
}