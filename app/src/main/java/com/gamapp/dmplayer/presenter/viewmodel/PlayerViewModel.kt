package com.gamapp.dmplayer.presenter.viewmodel

import androidx.lifecycle.ViewModel
import com.gamapp.dmplayer.framework.player.PlayerConnection
import com.gamapp.dmplayer.framework.player.PlayerConnectionImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerConnectionImpl: PlayerConnectionImpl
) : ViewModel() {
    val playerConnection: PlayerConnection = playerConnectionImpl
    init {
        addCloseable(playerConnectionImpl)
    }
}