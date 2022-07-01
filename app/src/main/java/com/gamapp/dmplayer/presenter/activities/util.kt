package com.gamapp.dmplayer.presenter.activities

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun <T : Context> T.compose(content: @Composable () -> Unit): ComposeView {
    return ComposeView(this).apply {
        setContent(content)
    }
}

internal val MenuVerticalMargin = 48.dp
fun calculateTransformOrigin(
    parentBounds: IntRect,
    menuBounds: IntRect
): TransformOrigin {
    val pivotX = when {
        menuBounds.left >= parentBounds.right -> 0f
        menuBounds.right <= parentBounds.left -> 1f
        menuBounds.width == 0 -> 0f
        else -> {
            val intersectionCenter =
                (
                        kotlin.math.max(parentBounds.left, menuBounds.left) +
                                kotlin.math.min(parentBounds.right, menuBounds.right)
                        ) / 2
            (intersectionCenter - menuBounds.left).toFloat() / menuBounds.width
        }
    }
    val pivotY = when {
        menuBounds.top >= parentBounds.bottom -> 0f
        menuBounds.bottom <= parentBounds.top -> 1f
        menuBounds.height == 0 -> 0f
        else -> {
            val intersectionCenter =
                (
                        kotlin.math.max(parentBounds.top, menuBounds.top) +
                                kotlin.math.min(parentBounds.bottom, menuBounds.bottom)
                        ) / 2
            (intersectionCenter - menuBounds.top).toFloat() / menuBounds.height
        }
    }
    return TransformOrigin(pivotX, pivotY)
}


data class DropdownMenuPositionProvider(
    val contentOffset: DpOffset,
    val density: Density,
    val onPositionCalculated: (IntRect, IntRect) -> Unit = { _, _ -> }
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        // The min margin above and below the menu, relative to the screen.
        val verticalMargin = with(density) { MenuVerticalMargin.roundToPx() }
        // The content offset specified using the dropdown offset parameter.
        val contentOffsetX = with(density) { contentOffset.x.roundToPx() }
        val contentOffsetY = with(density) { contentOffset.y.roundToPx() }

        // Compute horizontal position.
        val toRight = anchorBounds.left + contentOffsetX
        val toLeft = anchorBounds.right - contentOffsetX - popupContentSize.width
        val toDisplayRight = windowSize.width - popupContentSize.width
        val toDisplayLeft = 0
        val x = if (layoutDirection == LayoutDirection.Ltr) {
            sequenceOf(
                toRight,
                toLeft,
                // If the anchor gets outside of the window on the left, we want to position
                // toDisplayLeft for proximity to the anchor. Otherwise, toDisplayRight.
                if (anchorBounds.left >= 0) toDisplayRight else toDisplayLeft
            )
        } else {
            sequenceOf(
                toLeft,
                toRight,
                // If the anchor gets outside of the window on the right, we want to position
                // toDisplayRight for proximity to the anchor. Otherwise, toDisplayLeft.
                if (anchorBounds.right <= windowSize.width) toDisplayLeft else toDisplayRight
            )
        }.firstOrNull {
            it >= 0 && it + popupContentSize.width <= windowSize.width
        } ?: toLeft

        // Compute vertical position.
        val toBottom = maxOf(anchorBounds.bottom + contentOffsetY, verticalMargin)
        val toTop = anchorBounds.top - contentOffsetY - popupContentSize.height
        val toCenter = anchorBounds.top - popupContentSize.height / 2
        val toDisplayBottom = windowSize.height - popupContentSize.height - verticalMargin
        val y = sequenceOf(toBottom, toTop, toCenter, toDisplayBottom).firstOrNull {
            it >= verticalMargin &&
                    it + popupContentSize.height <= windowSize.height - verticalMargin
        } ?: toTop

        onPositionCalculated(
            anchorBounds,
            IntRect(x, y, x + popupContentSize.width, y + popupContentSize.height)
        )
        return IntOffset(x, y)
    }
}


@Composable
fun Popup(
    show: State<Boolean>,
    onDismissRequest: () -> Unit,
    animationSpec: FiniteAnimationSpec<Float> = spring(),
    popupProperties: PopupProperties = remember {
        PopupProperties(focusable = true)
    },
    content: @Composable (value: Animatable<Float, AnimationVector1D>) -> Unit
) {
    val currentAnimationSpec by rememberUpdatedState(newValue = animationSpec)
    val transformOriginState = remember { mutableStateOf(TransformOrigin.Center) }
    val density = LocalDensity.current
    val positionProvider = remember {
        DropdownMenuPositionProvider(
            DpOffset(0.dp, 0.dp),
            density
        ) { parentBounds, menuBounds ->
            transformOriginState.value = calculateTransformOrigin(parentBounds, menuBounds)
        }
    }
    val animator = remember {
        Animatable(if (show.value) 1f else 0f)
    }
    LaunchedEffect(key1 = Unit) {
        snapshotFlow { show.value }.collectLatest { show ->
            val target = if (show) 1f else 0f
            if (show) {
                delay(50)
            }
            animator.animateTo(targetValue = target, animationSpec = currentAnimationSpec)
        }
    }
    val showContent by remember {
        derivedStateOf {
            animator.value != 0f || show.value
        }
    }
    if (showContent)
        Popup(
            popupPositionProvider = positionProvider,
            properties = popupProperties,
            onDismissRequest = onDismissRequest,
        ) {
            content(animator)
        }
}


@Preview
@Composable
fun TestPopup() {
    val show = remember {
        mutableStateOf(false)
    }
    val icon by remember {
        derivedStateOf {
            if (!show.value)
                Icons.Rounded.Visibility
            else Icons.Rounded.VisibilityOff
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(onClick = {
            show.value = !show.value
        }, modifier = Modifier.align(Alignment.Center)) {
            Icon(
                imageVector = icon,
                contentDescription = "change visibility",
                tint = Color.White
            )
            Popup(
                show = show,
                animationSpec = spring(stiffness = 800f),
                onDismissRequest = { show.value = !show.value }) {
                Box(modifier = Modifier
                    .graphicsLayer {
                        alpha = it.value
                    }
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.White)
                    .width(100.dp)
                    .height(200.dp)
                )
            }
        }
    }

}

