package com.gamapp.dmplayer.presenter.ui.screen.dialog

import android.widget.Button
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gamapp.dmplayer.presenter.ui.theme.light
import kotlinx.coroutines.launch

@Composable
fun WarningDialog(
    visible: Boolean,
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    onFinish: () -> Unit,
    accept: String,
    cancel: String,
    dialog: String,
    cancelBtnColors: ButtonColors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onSurface),
    acceptBtnColors: ButtonColors = ButtonDefaults.buttonColors(),
) {
    val animate = remember {
        Animatable(initialValue = 0f)
    }
    val scope = rememberCoroutineScope()
    fun exit() {
        scope.launch {
            animate.animateTo(0f, tween(200))
            onFinish()
        }
    }

    fun enter() {
        scope.launch {
            animate.animateTo(1f, tween(300))
        }
    }
    if (visible)
        Dialog(
            onDismissRequest = { exit() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            LaunchedEffect(key1 = visible) {
                if (visible)
                    enter()
            }
            Box(modifier = Modifier
                .fillMaxSize()
                .removeClick {
                    exit()
                }) {
                Card(
                    modifier = Modifier
                        .graphicsLayer {
                            translationY = (1 - animate.value) * 100f
                            alpha = animate.value
                        }
                        .fillMaxWidth(0.8f)
                        .align(Alignment.Center)
                        .removeClick {},
                    elevation = 8.dp,
                    shape = RoundedCornerShape(25.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = dialog,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.W400
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .width(250.dp)
                                .height(80.dp)
                                .align(Alignment.CenterHorizontally),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onAccept()
                                    exit()
                                }, modifier = Modifier
                                    .width(100.dp)
                                    .height(50.dp)
                                    .align(Alignment.CenterVertically),
                                shape = RoundedCornerShape(15.dp),
                                colors = acceptBtnColors
                            ) {
                                Text(
                                    text = accept,
                                    textAlign = TextAlign.Center,
                                    color = acceptBtnColors.contentColor(enabled = true).value
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    onCancel()
                                    exit()
                                }, modifier = Modifier
                                    .width(100.dp)
                                    .height(50.dp)
                                    .align(Alignment.CenterVertically),
                                shape = RoundedCornerShape(15.dp),
                                colors = cancelBtnColors
                            ) {
                                Text(
                                    text = cancel,
                                    textAlign = TextAlign.Center,
                                    color = cancelBtnColors.contentColor(enabled = true).value
                                )
                            }
                        }
                    }
                }

            }
        }
}