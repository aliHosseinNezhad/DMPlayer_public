package com.gamapp.domain.player_interface

import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow

suspend infix fun <T> MutableStateFlow<T>.emit(input: T) {
    emit(input)
}
infix fun <T> MutableStateFlow<T>.tryEmit(value: T) {
    this.tryEmit(value)
}

interface PlayerConnection : LifecycleEventObserver {
    val playerEvents: PlayerEvents
    val controllers: PlayerController
}

fun LifecycleOwner.setupPlayer(playerConnection: PlayerConnection){
    this.lifecycle.addObserver(playerConnection)
}