package com.gamapp.dmplayer.presenter.ui.screen.dialog

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.custom.CustomIconButton
import com.gamapp.custom.numberpicker.NumberPicker
import com.gamapp.dmplayer.framework.player.PlayWithTimer
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.theme.DarkColorPalette
import com.gamapp.dmplayer.presenter.ui.theme.DarkTypography

import com.gamapp.dmplayer.presenter.ui.theme.light
import com.gamapp.dmplayer.presenter.viewmodel.PlayWithTimerViewModel
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel


@Composable
fun PlayWithTimerDialog(show: MutableState<Boolean>) {
    val playerViewModel = hiltViewModel<PlayerViewModel>()
    if (show.value) {
        fun finish() {
            show.value = false
        }
        Dialog(
            onDismissRequest = {
                finish()
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true
            )
        ) {
            val numbers = listOf(1, 2, 3, 4, 5, 8, 10, 15, 18, 20, 25, 30, 40, 50, 60, 80, 100)
            var selectedNumber by remember {
                mutableStateOf(numbers[0])
            }
            val viewModel: PlayWithTimerViewModel = hiltViewModel()
            MaterialTheme(
                colors = DarkColorPalette,
                typography = DarkTypography,
                shapes = Shapes()
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .clickable(interactionSource = remember {
                        MutableInteractionSource()
                    }, indication = null) { finish() }) {
                    Box(
                        modifier = Modifier
                            .width(350.dp)
                            .align(Center)
                            .clip(RoundedCornerShape(25.dp))
                            .background(Color.Black.copy(0.8f))
                            .clickable(interactionSource = remember {
                                MutableInteractionSource()
                            }, indication = null) {},
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Spacer(modifier = Modifier.requiredHeight(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center) {
                                val initialized =
                                    (viewModel.timerState.value == PlayWithTimer.TaskState.NotInitialized)
                                CustomIconButton(
                                    onClick = {},
                                    enabled = false,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .align(CenterVertically)

                                ) {
                                    val timer =
                                        if (viewModel.timerState.value == PlayWithTimer.TaskState.Started)
                                            R.drawable.ic_timer_enable
                                        else R.drawable.ic_timer_disable
                                    Image(
                                        painter = painterResource(id = timer),
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        colorFilter = ColorFilter.tint(
                                            MaterialTheme.colors.onSurface.copy(
                                                0.6f
                                            )
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                val timeToEnd =
                                    with(viewModel) { duration.value - currentTime.value }
                                val text =
                                    if (initialized) "Music will stop after the selected time" else "$timeToEnd seconds to stop music"
                                Text(
                                    text = text,
                                    textAlign = TextAlign.Start,
                                    maxLines = 1,
                                    modifier = Modifier
                                        .align(CenterVertically),
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.typography.caption.color,
                                    fontSize = 13.sp
                                )
                            }
                            if (viewModel.timerState.value == PlayWithTimer.TaskState.NotInitialized)
                                Box(
                                    modifier = Modifier
                                        .width(300.dp)
                                        .height(60.dp),
                                    contentAlignment = Center
                                ) {
                                    val color = MaterialTheme.typography.body1.color
                                    AndroidView(
                                        factory = { it ->
                                            NumberPicker(it, color, numbers).apply {
                                                setOnSelectedItemChangeListener {
                                                    selectedNumber = numbers[it]
                                                }
                                            }
                                        }, modifier = Modifier.fillMaxSize()
                                    )
                                }
                            Spacer(modifier = Modifier.requiredHeight(8.dp))
                            Row(
                                modifier = Modifier
                                    .width(250.dp)
                                    .align(Alignment.CenterHorizontally),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val context = LocalContext.current
                                val initialized =
                                    viewModel.timerState.value != PlayWithTimer.TaskState.NotInitialized
                                Button(
                                    onClick = {
                                        if (!initialized) {
                                            viewModel.setTask(selectedNumber * 60L)
                                            if (playerViewModel.isPlaying.value) {
                                                Toast.makeText(
                                                    context,
                                                    "timer will start after playing a song!",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        } else {
                                            viewModel.stopTask()
                                        }

                                    }, modifier = Modifier
                                        .width(100.dp)
                                        .requiredHeight(50.dp)
                                        .align(Alignment.CenterVertically),
                                    shape = RoundedCornerShape(15.dp)
                                ) {
                                    Text(
                                        text = if (!initialized) "Set" else "Stop",
                                        textAlign = TextAlign.Center,
                                        color = light
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        finish()
                                    }, modifier = Modifier
                                        .width(100.dp)
                                        .requiredHeight(50.dp)
                                        .align(Alignment.CenterVertically),
                                    shape = RoundedCornerShape(15.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Transparent)
                                ) {
                                    Text(
                                        text = "exit",
                                        textAlign = TextAlign.Center,
                                    )
                                }

                            }
                            Spacer(modifier = Modifier.requiredHeight(16.dp))
                        }
                    }


                }
            }
        }
    }
}