package com.gamapp.domain.usecase.player


import com.gamapp.domain.player_interface.PlayerConnector
import javax.inject.Inject



class FastForwardUseCase @Inject constructor(private val player: PlayerConnector) {
     suspend fun invoke(){
        player.nextMusic()
    }
}