package com.gamapp.dmplayer.presenter.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.DialogsState

val LocalDialogManager = staticCompositionLocalOf {
    DialogsState()
}