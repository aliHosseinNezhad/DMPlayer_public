package com.gamapp.domain.usecase.player

import com.gamapp.domain.player_interface.PlayerConnector
import com.gamapp.domain.player_interface.PlayerData
import com.gamapp.domain.player_interface.RepeatMode
import com.gamapp.domain.player_interface.RepeatMode.*
import javax.inject.Inject



class RepeatModeUseCase @Inject constructor(
    private val player: PlayerConnector,
    private val playerData: PlayerData
) {
    suspend fun invoke() {
        val mode =  when(playerData.repeatMode.value){
            OFF -> ALL
            ALL -> ONE
            ONE -> OFF
        }
        invoke(mode)
    }
    suspend fun invoke(mode: RepeatMode){
        player.setRepeatMode(mode)
    }
}