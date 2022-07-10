package com.gamapp.dmplayer.presenter.ui.screen.dialog.add_musics_to_queues_dialog

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.data.utils.DefaultQueueIds
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.models.QueueModel
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.RemoveDialogData
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.WarningDialogTexts.RemoveQueue
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.dialogs
import com.gamapp.dmplayer.presenter.ui.screen.dialog.removeClick
import com.gamapp.dmplayer.presenter.ui.screen.elements.DialogQueueItem
import com.gamapp.dmplayer.presenter.ui.theme.*
import com.gamapp.dmplayer.presenter.viewmodel.QueueViewModel
import com.gamapp.domain.models.BaseTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Composable
fun AddMusicsToQueuesDialog(
    show: Boolean,
    tracks: List<BaseTrack>,
    finish: () -> Unit,
) {
    val animation = updateTransition(targetState = show, label = "transition").animateFloat(
        label = "transitionAddToQueue",
        transitionSpec = { tween(500) }) {
        if (it) 1f else 0f
    }
    val visible by remember {
        derivedStateOf {
            animation.value != 0f
        }
    }
    if (visible) {
        val dialog = dialogs()
        val ioScope = rememberCoroutineScope {
            Dispatchers.IO
        }
        val viewModel = hiltViewModel<QueueViewModel>()
        val queueList by remember {
            viewModel.loadQueues()
        }.observeAsState(initial = listOf())

        var showDelete by remember {
            mutableStateOf(false)
        }
        Dialog(
            onDismissRequest = {
                if (showDelete) {
                    showDelete = false
                } else {
                    finish()
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .removeClick {
                        finish()
                    }, contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .wrapContentSize()
                        .removeClick { }
                        .graphicsLayer {
                            alpha = animation.value
                            translationY = 200 * (1f - animation.value)
                        },
                    shape = RoundedCornerShape(25.dp),
                ) {
                    val background = MaterialTheme.colors.dialog
                    Box(
                        modifier = Modifier
                            .drawWithContent {
                                drawContent()
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        0f to background,
                                        0.03f to Color.Transparent,
                                        0.95f to Color.Transparent,
                                        1f to background
                                    )
                                )
                            }
                            .fillMaxWidth(0.8f)
                            .background(MaterialTheme.colors.dialog)
                            .heightIn(maxHeight * 0.5f, maxHeight * 0.8f)
                    ) {
                        val defaults by remember {
                            derivedStateOf {
                                queueList.filter { it.default }.map {
                                    it to {
                                        ioScope.launch {
                                            viewModel.queueInteracts.addTrack(it.id, tracks)
                                        }
                                    }
                                }
                            }
                        }
                        val queues by remember {
                           derivedStateOf {
                               queueList.filter { !it.default }.map {
                                   it to {
                                       ioScope.launch {
                                           viewModel.queueInteracts.addTrack(it.id, tracks)
                                       }
                                   }
                               }
                           }
                        }
                        LazyColumn(
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .wrapContentHeight()
                        ) {
                            item {
                                CreateQueueButton()
                            }
                            item {
                                AddTrackToQueueText(tracks = tracks)
                                QueueDivider()
                            }
                            items(defaults) { item ->
                                DialogQueueItem(
                                    queueModel = item.first,
                                    onClick = {
                                        item.second()
                                    },
                                    tint = MaterialTheme.colors.onContent,
                                    showRemoveButton = false,
                                    onRemove = {},
                                    defaultImage = if (item.first.id == DefaultQueueIds.Favorite)
                                        R.drawable.ic_heart_filled else
                                        R.drawable.ic_queues
                                )

                            }
                            item {
                                QueueDivider()
                            }
                            items(queues) { item ->
                                DialogQueueItem(
                                    queueModel = item.first,
                                    onClick = { item.second() },
                                    tint = MaterialTheme.colors.onContent,
                                    showRemoveButton = showDelete,
                                    onRemove = {
                                        showDelete = false
                                        dialog.show(
                                            RemoveDialogData(
                                                texts = RemoveQueue,
                                                onAccept = {
                                                    viewModel.remove(item.first)
                                                },
                                                onCancel = {}
                                            )
                                        )
                                    },
                                    onLongClick = {
                                        showDelete = true
                                    },
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
        BackHandler(showDelete) {
            finish()
        }

    }
}