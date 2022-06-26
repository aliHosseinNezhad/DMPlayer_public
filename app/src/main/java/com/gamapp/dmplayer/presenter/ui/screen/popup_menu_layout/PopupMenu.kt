package com.gamapp.dmplayer.presenter.ui.screen.popup_menu_layout


import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.cardview.widget.CardView
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.gamapp.dmplayer.presenter.ui.theme.popupColor
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp

@Composable
fun popupProvider() = remember {
    object : PopupPositionProvider {
        override fun calculatePosition(
            anchorBounds: IntRect,
            windowSize: IntSize,
            layoutDirection: LayoutDirection,
            popupContentSize: IntSize,
        ): IntOffset {
            val x = anchorBounds.topRight.x - popupContentSize.width
            val y = anchorBounds.topRight.y
            return IntOffset(x, y)
        }
    }
}

interface PopupManager {
    fun exit()
}


@Composable
fun PopupMenu(
    show: MutableState<Boolean>,
    content: @Composable PopupManager.() -> Unit,
) {
    if (show.value) {
        val coroutineScope = rememberCoroutineScope {
            Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        }
        var value by remember {
            mutableStateOf(0f)
        }

        fun enter() {
            coroutineScope.launch {
                animate(
                    initialValue = 0.00f,
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 220) {
                        val k = 2f
                        1f - exp(-k * it) * cos(PI / 2 * it).toFloat()
                    }) { v, _ ->
                    value = v
                }
            }

        }

        fun exit() {
            coroutineScope.launch {
                animate(1f, 0.0f, animationSpec = tween(100) {
                    val k = 2f
                    1f - exp(-k * it) * cos(PI / 2 * it).toFloat()
                }) { v, _ ->
                    value = v
                }
                show.value = false
            }
        }

        val popupManager = remember {
            object : PopupManager {
                override fun exit() {
                    exit()
                }
            }
        }

        val background = MaterialTheme.colors.popupColor.toArgb()
        val density = LocalDensity.current
        Popup(
            popupPositionProvider = popupProvider(),
            properties = PopupProperties(
                usePlatformDefaultWidth = false,
                focusable = true
            ),
            onDismissRequest = {
                exit()
            }
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .widthIn(max = 250.dp)
            ) {


                LaunchedEffect(key1 = show.value) {
                    if (show.value)
                        enter()
                }
                BackHandler {
                    coroutineScope.launch {
                        exit()
                    }
                }
                AndroidView(factory = {
                    FrameLayout(it).apply {
                        clipChildren = false
                        layoutParams = ViewGroup.LayoutParams(-2, -2)
                        addView(CardView(it).apply {
                            layoutParams = FrameLayout.LayoutParams(-1, -2).apply {
                                gravity = Gravity.CENTER
                                val margin = with(density) { 12.dp.roundToPx() }
                                marginStart = margin
                                marginEnd = margin
                                topMargin = margin
                                bottomMargin = margin
                            }
                            setCardBackgroundColor(background)
                            cardElevation = with(density) { 4.dp.toPx() }
                            radius = with(density) { 16.dp.toPx() }
                            addView(ComposeView(context).apply {
                                setContent {
                                    popupManager.content()
                                }
                            })
                        })
                    }
                }, modifier = Modifier
                    .wrapContentSize(), update = {
                    it.getChildAt(0).run {
                        (this as CardView).run {
                            setCardBackgroundColor(background)
                            alpha = value
                            pivotX = this.width.toFloat()
                            pivotY = 0f
                            scaleX = value + (1 - value) * 0.8f
                            scaleY = value + (1 - value) * 0.8f
                        }
                    }

                })
            }
        }
    }
}

@Composable
fun PopupMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    fontSize: TextUnit = 13.sp,
    textAlign: TextAlign = TextAlign.Start,
) {
    Box(modifier = modifier
        .clickable {
            onClick()
        }
        .padding(horizontal = 16.dp)) {
        Text(
            text = text,
            textAlign = textAlign,
            modifier = Modifier
                .widthIn(200.dp, 300.dp)
                .align(Alignment.CenterStart),
            fontSize = fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}