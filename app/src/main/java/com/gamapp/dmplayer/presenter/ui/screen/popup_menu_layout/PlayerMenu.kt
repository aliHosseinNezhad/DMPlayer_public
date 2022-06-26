package com.gamapp.dmplayer.presenter.ui.screen.popup_menu_layout

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.dialogs
import com.gamapp.dmplayer.presenter.ui.screen.menu.string
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp

class ThreeItem<F, S, T>(val first: F, val second: S, val third: T) {
    constructor(pair: Pair<F, S>, third: T) : this(pair.first, pair.second, third)
}

infix fun <F, S, T> Pair<F, S>.to(third: T): ThreeItem<F, S, T> {
    return ThreeItem(this, third)
}

@Composable
fun Offset.toDpOffset(): DpOffset {
    val x = with(LocalDensity.current) {
        x.toDp()
    }
    val y = with(LocalDensity.current) {
        (-y / 4).toDp()
    }
    return DpOffset(x, y)
}

fun animation(value: Float): Float {
    val x = value * 4
    return 1f - exp(-x) * cos(PI / 8 * x).toFloat()
}

@Composable
fun PlayerMenu(show: MutableState<Boolean>, nav: NavHostController) {
    val dialog = dialogs()
    val viewModel: PlayerViewModel = hiltViewModel()
    val currentTrack = remember(viewModel) {
        viewModel.currentTrack
    }.collectAsState()
    TextPopupMenu(
        show = show.value,
        popupList = remember(viewModel.playerMenu) {
            viewModel.playerMenu.menu(currentTrack, dialog, nav)
        }.string(),
        onDismiss = {
            show.value = false
        }
    )
}