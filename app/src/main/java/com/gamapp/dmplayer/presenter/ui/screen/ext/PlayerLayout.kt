package com.gamapp.dmplayer.presenter.ui.screen.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.gamapp.dmplayer.presenter.ui.screen.player.PlayerData

const val TAG = "TrackPlayerLayout"

class Ref<T>(var value: T)
interface TrackPlayerScope {
    val constraints: Constraints
    val motionHeight: Ref<Int>
}

class TrackPlayerScopeImpl : TrackPlayerScope {
    override var constraints: Constraints = Constraints.fixed(0, 0)
    override val motionHeight: Ref<Int> = Ref(0)
}

object TrackPlayer {
    const val DynamicGradient = "dynamicGradient"
    const val PlayList = "playList"
    const val TrackContent = "trackContent"
    const val AddToFavorite = "addToFavorite"
    const val Menu = "menu"
    const val HeartAnimation = "heartAnimation"
    const val Pager = "pager"
    const val Controller = "controllerHeight"
    const val MinimalController = "minimalController"
    const val TrackDetails = "trackDetail"
}

fun List<Measurable>.item(id: Any?): Measurable? {
    return firstOrNull {
        it.layoutId == id
    }
}

fun List<Measurable>.measure(key: Any?, constraints: Constraints): Placeable? {
    return firstOrNull {
        it.layoutId == key
    }?.measure(constraints)
}

@Composable
fun trackPlayerMeasurePolicy(
    scope: TrackPlayerScopeImpl,
    top: Dp,
    bottom: Dp,
    placeController: State<Boolean>,
) = remember(top, bottom, scope) {
    MeasurePolicy { measurables, constraints ->
        scope.constraints = constraints
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val controller = measurables.measure(TrackPlayer.Controller, childConstraints)!!
        val minimalController =
            measurables.measure(TrackPlayer.MinimalController, childConstraints)!!
        val menu = measurables.measure(TrackPlayer.Menu, childConstraints)!!
        val dynamicGradient = measurables.measure(TrackPlayer.DynamicGradient, childConstraints)!!
        var otherHeights = 0
        otherHeights += controller.height
        otherHeights += menu.height
        otherHeights += minimalController.height / 2

        val leftHeight =
            (constraints.maxHeight - otherHeights - top.roundToPx() - bottom.roundToPx()).coerceAtLeast(
                0
            )
        val width = constraints.maxWidth
        scope.motionHeight.value =
            (constraints.maxHeight - minimalController.height - bottom.roundToPx()) - ((top.roundToPx() + menu.height))
        val trackContent =
            measurables.measure(
                TrackPlayer.TrackContent, scope.constraints.copy(
                    minWidth = width,
                    maxWidth = width,
                    minHeight = leftHeight,
                    maxHeight = leftHeight
                )
            )!!

        layout(constraints.maxWidth, constraints.maxHeight) {
            dynamicGradient.place(0, 0)
            var t = top.roundToPx()
            menu.place(
                0,
                t
            )
            t += menu.height
            trackContent.place(0, t, 1f)
            var y = constraints.maxHeight - bottom.roundToPx() - minimalController.height
            minimalController.place(
                0,
                y,
            )
            y -= controller.height - minimalController.height / 2
            if (placeController.value)
                controller.place(
                    0,
                    y
                )
        }
    }
}

@Composable
inline fun TrackPlayerLayout(
    modifier: Modifier,
    bottom: Dp,
    top: Dp,
    data: PlayerData,
    content: @Composable (TrackPlayerScope.() -> Unit),
) {
    val scope = remember {
        TrackPlayerScopeImpl()
    }
    val placeController = derivedStateOf {
        data.playerExpandContentAlpha != 0f
    }
    val measurePolicy =
        trackPlayerMeasurePolicy(scope, top, bottom, placeController = placeController)
    Layout(measurePolicy = measurePolicy, modifier = modifier, content = {
        scope.content()
    })
}