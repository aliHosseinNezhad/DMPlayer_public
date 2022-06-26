package com.gamapp.dmplayer.presenter

import androidx.compose.runtime.State

sealed class TopBarType(var name: String) {
    object None : TopBarType("")
    class Selection(val checkbox: (Boolean) -> Unit, val isChecked: State<Boolean>) :
        TopBarType("")
}