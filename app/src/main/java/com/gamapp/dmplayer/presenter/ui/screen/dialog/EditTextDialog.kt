package com.gamapp.dmplayer.presenter.ui.screen.dialog

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gamapp.dmplayer.presenter.ui.theme.light
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

fun Modifier.removeClick(onClick: () -> Unit) = composed {
    clickable(indication = null, interactionSource = remember {
        MutableInteractionSource()
    }) {
        onClick()
    }
}

@Composable
fun EditTextDialog(
    show:Boolean,
    title: String,
    label: String,
    acceptBtn: String,
    cancelBtn: String,
    onAccept: (String) -> Unit,
    onCancel: () -> Unit,
    initialValue: String = "",
    finish: () -> Unit
) {
    if (show) {
        var text by remember {
            mutableStateOf(initialValue)
        }
        val animationScope = rememberCoroutineScope {
            Executors.newSingleThreadScheduledExecutor().asCoroutineDispatcher()
        }
        var value by remember {
            mutableStateOf(0f)
        }

        fun exit() {
            animationScope.launch {
                delay(50)
                animate(
                    initialValue = 1f,
                    targetValue = 0f,
                    animationSpec = tween(250)
                ) { v, _ ->
                    value = v
                }
                finish()
            }
        }
        Dialog(
            onDismissRequest = { exit() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            LaunchedEffect(key1 = show) {
                animationScope.launch {
                    if (show)
                        animate(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = tween(450)
                        ) { v, _ ->
                            value = v
                        }

                }
            }
            Box(modifier = Modifier
                .fillMaxSize()
                .removeClick {
                    exit()
                }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Center)
                        .removeClick {}
                        .graphicsLayer {
                            alpha = value
                            translationY = 100 * (1 - value)
                        },
                    elevation = 8.dp,
                    shape = RoundedCornerShape(25.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            color = MaterialTheme.colors.onSurface.copy(0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Normal,
                            fontSize = 19.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = text,
                            onValueChange = {
                                text = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp),
                            label = {
                                Text(text = label, color = Color.Gray, maxLines = 1)
                            },
                            shape = RoundedCornerShape(15.dp),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .width(250.dp)
                                .height(80.dp)
                                .align(CenterHorizontally),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    onAccept(text)
                                    exit()
                                }, modifier = Modifier
                                    .width(100.dp)
                                    .height(50.dp)
                                    .align(CenterVertically),
                                shape = RoundedCornerShape(15.dp)
                            ) {
                                Text(
                                    text = acceptBtn,
                                    textAlign = TextAlign.Center,
                                    color = light
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    onCancel()
                                    exit()
                                }, modifier = Modifier
                                    .width(100.dp)
                                    .height(50.dp)
                                    .align(CenterVertically),
                                shape = RoundedCornerShape(15.dp),
                                colors = ButtonDefaults.outlinedButtonColors()
                            ) {
                                Text(
                                    text = cancelBtn,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }

                    }
                }

            }
        }
    }
}