package com.gamapp.domain.usecase.player

import com.gamapp.domain.player_interface.PlayerController
import javax.inject.Inject

class FastForwardUseCase @Inject constructor(
     private val controller: PlayerController
)  {
     operator fun invoke() {
          controller.fastForward()
     }
}