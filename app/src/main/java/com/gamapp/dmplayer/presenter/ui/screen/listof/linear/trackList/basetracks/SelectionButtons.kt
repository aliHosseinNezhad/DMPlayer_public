package com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.*
import com.gamapp.dmplayer.presenter.ui.screen.dialog.showAddToQueueDialog
import com.gamapp.dmplayer.presenter.ui.screen.dialog.showRemoveTrackDialog
import com.gamapp.dmplayer.presenter.ui.theme.primary
import com.gamapp.dmplayer.presenter.ui.screen.ext.TopClip
import com.gamapp.dmplayer.presenter.utils.SelectionManager
import com.gamapp.dmplayer.presenter.viewmodel.AppViewModel
import com.gamapp.dmplayer.presenter.viewmodel.TracksViewModel
import com.gamapp.domain.model_usecase.remove
import com.gamapp.domain.models.TrackModel
import kotlinx.coroutines.launch

data class Button(
    val title: String,
    val icon: Int,
    val onClick: () -> Unit,
    val size: Dp,
    val padding: Dp = 0.dp
)

@Composable
fun BoxScope.TrackSelectionButtons(
    manager: SelectionManager<TrackModel>
) {
    val viewModel = viewModel<AppViewModel>()
    val trackViewModel = hiltViewModel<TracksViewModel>()
    val dialog = dialogs()
    val animateValue by updateTransition(
        targetState = manager.onSelectionMode,
        label = "animationValue"
    ).animateFloat(label = "value", transitionSpec = {
        tween(500)
    }) {
        if (it) 1f else 0f
    }
    val scope = rememberCoroutineScope()

    fun exitSelection() {
        manager.cancelSelection()
    }
    Box(modifier = Modifier
        .align(Alignment.BottomCenter)
        .zIndex(2f)
        .fillMaxWidth()
        .requiredHeight(90.dp)
        .graphicsLayer {
            translationY = (1 - animateValue) * 90.dp.toPx()
            alpha = animateValue
        }
        .clip(TopClip())
        .background(primary)) {
        val enabled = manager.selectionList.isNotEmpty()
        val buttons = listOf(
            Button(
                title = stringResource(R.string.play),
                icon = R.drawable.round_play_arrow_24,
                onClick = {
                    val playList = manager.selectionList
                    if (playList.isNotEmpty()) {
                        scope.launch {
                            viewModel.setAndPlay(playList, playList.first())
                            exitSelection()
                        }
                    }

                },
                size = 30.dp
            ),
            Button(
                title = stringResource(R.string.add_to),
                icon = R.drawable.round_add_24,
                onClick = {
                    dialog.showAddToQueueDialog(manager.selectionList) {}
                },
                size = 30.dp
            ),
            Button(
                title = stringResource(R.string.menu_share),
                icon = R.drawable.round_share_24,
                onClick = {
                    TODO()
                    exitSelection()
                },
                size = 30.dp,
                padding = 4.dp
            ),
            Button(
                title = stringResource(R.string.remove_lowercase),
                icon = R.drawable.round_clear_24,
                onClick = {
                    dialog.showRemoveTrackDialog(manager.selectionList) {
                        it.remove(trackViewModel.interacts)
                    }
                },
                size = 30.dp,
                padding = 2.dp
            ),
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 25.dp)
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            buttons.forEach { button ->
                CustomIconButton(
                    onClick = {
                        button.onClick()
                    },
                    enabled = enabled,
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            painter = painterResource(id = button.icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(button.size)
                                .padding(button.padding)
                                .align(Alignment.CenterHorizontally),
                            tint = Color.White
                        )
                        Text(
                            text = button.title,
                            maxLines = 1,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }

                }
            }
        }
    }
}


