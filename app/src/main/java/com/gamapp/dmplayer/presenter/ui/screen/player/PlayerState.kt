package com.gamapp.dmplayer.presenter.ui.screen.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.gamapp.dmplayer.RegisterReceiver
import com.gamapp.dmplayer.filter
import com.gamapp.dmplayer.presenter.ui.theme.primary
import com.gamapp.dmplayer.presenter.ui.theme.secondary
import com.gamapp.dmplayer.rememberReceiver
import com.gamapp.domain.ACTIONS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp

fun animation(value: Float): Float {
    val k = 1.3f
    return 1f - exp(-value * k) * cos(PI / (2) * value).toFloat()
}

data class PlayerState constructor(val dataState: State<PlayerData>) {
    val data by dataState
    private val animatable = Animatable(0f)
    suspend fun showPlayer() {
        with(dataState.value) {
            if (playerAlpha != 1f) {
                animatable.snapTo(playerAlpha)
                animatable.animateTo(targetValue = 1f, animationSpec = tween(400)) {
                    playerAlpha = value
                }
            }
        }
    }
    suspend fun hidePlayer() {
        with(dataState.value) {
            if (swipeableState.currentValue != Minimized)
                swipeableState.animateTo(Minimized)
            if (playerAlpha != 0f) {
                animatable.snapTo(playerAlpha)
                animatable.animateTo(targetValue = 0f, animationSpec = tween(300)) {
                    playerAlpha = value
                }
            }
        }
    }

    @Composable
    fun Init() {
        val scope = rememberCoroutineScope()
        val playerVisibilityReceiver = remember {
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    when (intent?.action ?: return) {
                        ACTIONS.PLAYER.SHOW -> {
                            scope.launch {
                                showPlayer()
                            }
                        }
                        ACTIONS.PLAYER.HIDE -> {
                            scope.launch {
                                hidePlayer()
                            }
                        }
                    }
                }

            }
        }
        RegisterReceiver(receiver = playerVisibilityReceiver, filter = filter {
            addAction(ACTIONS.PLAYER.HIDE)
            addAction(ACTIONS.PLAYER.SHOW)
        })
    }

}

data class PlayerData(
    val size: State<IntSize>,
    val density: Density
) {
    val min = with(density) { 60.dp.toPx() }
    val max by derivedStateOf {
        size.value.height.toFloat() * 0.6f
    }
    val anchors by derivedStateOf {
        mapOf(max to Expanded, min to Minimized)
    }
    val swipeableState: SwipeableState<Int> = SwipeableState(
        initialValue = Minimized,
        animationSpec = tween(250, 0, easing = {
            animation(it)
        })
    )
    val motionValue by derivedStateOf {
        ((swipeableState.offset.value - min) / (max - min)).coerceIn(0f, 1f)
    }

    val playerExpandContentAlpha by derivedStateOf {
        (motionValue - 0.5f).coerceIn(0f, 0.5f) / 0.5f
    }
    val playerShrinkContentAlpha by derivedStateOf {
        1f - playerExpandContentAlpha
    }

    val minimalButtonsAlpha by derivedStateOf {
        (1 - motionValue - 0.5f).coerceIn(0f, 0.5f) / 0.5f
    }


    var playerAlpha by mutableStateOf(1f)

    val expandedState = derivedStateOf {
        swipeableState.currentValue == Expanded
    }
    val expanded by expandedState

    var showPlayList by mutableStateOf(false)

    val enableSwipeableState by derivedStateOf {
        !showPlayList
    }

    val colors: MutableState<List<Color>> = mutableStateOf(
        mutableListOf(
            primary,
            secondary
        )
    )


}

