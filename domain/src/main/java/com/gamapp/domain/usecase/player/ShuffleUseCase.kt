package com.gamapp.domain.usecase.player

import javax.inject.Inject

class ShuffleUseCase @Inject constructor(){
    suspend operator fun invoke(){}

    suspend operator fun invoke(enable: Boolean){}
}