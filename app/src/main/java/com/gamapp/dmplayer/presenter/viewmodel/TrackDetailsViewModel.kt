package com.gamapp.dmplayer.presenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.models.TrackUpdate
import com.gamapp.domain.usecase.interacts.Interacts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackDetailViewModel @Inject constructor(val act: Interacts) : ViewModel() {

    fun update(track: TrackModel, title: String) = viewModelScope.launch {
        act.track.update.invoke(
            track, items = listOf(
                TrackUpdate.Title(value = title)
            )
        )
    }
}