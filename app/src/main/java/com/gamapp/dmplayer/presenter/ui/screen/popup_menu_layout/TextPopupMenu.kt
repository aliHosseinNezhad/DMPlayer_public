package com.gamapp.dmplayer.presenter.ui.screen.popup_menu_layout

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.gamapp.dmplayer.presenter.ui.theme.popupColor
import com.gamapp.layout.PopupLayout


@Composable
fun TextPopupMenu(
    show: Boolean,
    onDismiss: () -> Unit,
    popupList: List<Pair<String, () -> Unit>>,
) {
    val popupPositionProvider = remember {
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
    val properties = remember {
        PopupProperties(focusable = true)
    }
    val animation = updateTransition(targetState = show, label = null).animateFloat(
        label = "",
        transitionSpec = {
            tween(400)
        }) {
        if (it) 1f else 0f
    }
    val showPopup by remember {
        derivedStateOf {
            animation.value != 0f
        }
    }
    if (showPopup)
        Popup(
            popupPositionProvider = popupPositionProvider,
            onDismissRequest = onDismiss,
            properties = properties
        ) {
            val background = MaterialTheme.colors.popupColor
            PopupLayout(
                modifier = Modifier
                    .widthIn(100.dp)
                    .wrapContentHeight()
                    .graphicsLayer {
                        val scale = 1f * (animation.value) + 0.8f * (1 - animation.value)
                        scaleX = scale
                        scaleY = scale
                        alpha = animation.value
                        shadowElevation = 3.dp.toPx()
                        clip = true
                        shape = RoundedCornerShape(25.dp)
                    }
                    .background(background)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides contentColorFor(
                        backgroundColor = MaterialTheme.colors.surface
                    )
                ) {
                    popupList.forEach {
                        Text(
                            text = it.first, modifier = Modifier
                                .clickable(interactionSource = remember {
                                    MutableInteractionSource()
                                }, indication = rememberRipple(), onClick = {
                                    it.second()
                                    onDismiss()
                                })
                                .padding(horizontal = 16.dp)
                                .height(50.dp)
                                .wrapContentHeight(align = Alignment.CenterVertically)
                                .wrapContentWidth(align = Alignment.Start),
                            textAlign = TextAlign.Center,
                            color = LocalContentColor.current
                        )
                    }
                }
            }
            BackHandler {
                onDismiss()
            }
        }
}
