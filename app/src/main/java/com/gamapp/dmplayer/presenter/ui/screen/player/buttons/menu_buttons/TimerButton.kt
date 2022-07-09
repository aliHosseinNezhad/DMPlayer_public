package com.gamapp.dmplayer.presenter.ui.screen.player.buttons.menu_buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.dialog.PlayWithTimerDialog
import com.gamapp.dmplayer.presenter.viewmodel.PlayWithTimerViewModel

@Composable
fun TimerButton() {
    val viewModel:PlayWithTimerViewModel = hiltViewModel()
    var showPlayWithTimerDialog = remember {
        mutableStateOf(false)
    }
    val icon by remember {
        derivedStateOf {
            R.drawable.ic_timer_disable
//            if (viewModel.timerState.value == PlayWithTimer.TaskState.Started)
//                R.drawable.ic_timer_enable
//            else  R.drawable.ic_timer_disable
        }
    }
    CustomIconButton(
        onClick = {
           showPlayWithTimerDialog.value = true
        },
        modifier = Modifier
            .requiredSize(50.dp),
    ) {
        Image(
            painter = rememberVectorPainter(image = ImageVector.vectorResource(id = icon)),
            contentDescription = null,
            modifier = Modifier
                .size(20.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground.copy(0.8f)),
            contentScale = ContentScale.Inside
        )
    }
    PlayWithTimerDialog(show = showPlayWithTimerDialog)
}