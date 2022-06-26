package com.gamapp.custom

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.IconButton
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

import kotlin.math.PI
import kotlin.math.cos

class Updater {
    private val _update: Channel<Boolean> = Channel()
    internal val update = _update.receiveAsFlow()
    fun start() {
        _update.trySend(true)
    }
}

@Composable
fun Test() {
    IconButton(onClick = { /*TODO*/ }) {

    }
}

@Composable
fun CustomIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    val updater = remember {
        Updater()
    }
    val animatable = remember {
        Animatable(0f)
    }
    var scale by remember {
        mutableStateOf(1f)
    }
    LaunchedEffect(key1 = Unit) {
        updater.update.collect {
            animatable.snapTo(0f)
            animatable.animateTo(
                1f,
                animationSpec = tween(durationMillis = 300, easing = LinearEasing)
            ) {
                val cos = cos(value * PI)
                scale = ((cos * cos).toFloat() * 0.2f + 0.8f)
            }
        }
    }
    val clickModifier = if (enabled) {
        val source = interactionSource ?: remember {
            MutableInteractionSource()
        }
        Modifier.combinedClickable(
            enabled = enabled,
            interactionSource = source,
            indication = rememberRipple(bounded = false, radius = 25.dp),
            role = Role.Image,
            onClick = {
                onClick()
                updater.start()
            }, onLongClick = onLongClick
        )
    } else Modifier
    Box(
        modifier = modifier
            .then(clickModifier)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}