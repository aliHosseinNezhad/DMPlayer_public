package com.gamapp.dmplayer.presenter.utils

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp

class CancelableCoroutine(coroutineScope: CoroutineScope) : CoroutineScope by coroutineScope {
    private var job: Job? = null
    var isWorking: Boolean = false
        private set

    fun start(content: suspend () -> Unit) {
        isWorking = true
        job?.cancel()
        job = launch {
            content()
            isWorking = false
        }
    }

    fun stop() {
        job?.cancel()
        isWorking = false
    }
}
@Composable
fun sharedMotion(
    startPosition: Offset,
    size: IntSize,
    statusBarHeight: Dp,
    finish: MutableState<Boolean>,
    stop: MutableState<Boolean> = remember {
        mutableStateOf(false)
    },
    onEnterAnimFinish: () -> Unit,
    onFinishAnimEnd: () -> Unit,
    onSetGraphicLayer: GraphicsLayerScope.(value: Float) -> Unit,
    duration: Long = 800L,
): Modifier = Modifier.composed {
    var currentSize by remember {
        mutableStateOf(null as IntSize?)
    }
    var expectedPosition by remember {
        mutableStateOf(null as Offset?)
    }
    var scaleX by remember {
        mutableStateOf(null as Float?)
    }
    var scaleY by remember {
        mutableStateOf(null as Float?)
    }
    var translationX by remember {
        mutableStateOf(null as Float?)
    }
    var translationY by remember {
        mutableStateOf(null as Float?)
    }
    var alpha by remember {
        mutableStateOf(1f)
    }
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val cancelableCoroutine = remember {
        CancelableCoroutine(scope)
    }

    fun animationSpec(value: Float): Float {
        val x = value * 4
        return 1f - exp(-x) * cos(PI / 8 * x).toFloat()
    }

    suspend fun startAnimation() {
        val df = startPosition - expectedPosition!!
        val dx =
            df.x - (currentSize!!.width / 2f - size.width / 2f)
        val dy = df.y - (currentSize!!.height +
                with(density) {
                    statusBarHeight
                        .toPx()
                }) / 2f
        val sX = size.width / currentSize!!.width.toFloat()
        val sY = size.height / currentSize!!.height.toFloat()
        val initialValue = 0
        val millis = 15L
        val total = duration / millis.toInt()
        var cos = 1f

        fun set() {
            alpha = cos
            translationX = dx * cos
            translationY = dy * cos
            scaleX = sX * cos + (1f - cos)
            scaleY = sY * cos + (1f - cos)
        }
        withContext(Dispatchers.Main){
            animate(initialValue = initialValue.toFloat(), targetValue = total.toFloat(), animationSpec = tween(duration.toInt())) { v, _ ->
                val x = v / total.toFloat()
                cos = animationSpec(1 - x)
                set()
            }
        }
        cos = 0f
        set()
        onEnterAnimFinish()
    }

    suspend fun finishAnimation(onEnd: () -> Unit) {
        val df = startPosition - expectedPosition!!
        val dx =
            df.x - (currentSize!!.width / 2f - size.width / 2f)
        val dy = df.y - with(density) {
            statusBarHeight
                .toPx()
        } - (currentSize!!.height - size.height) / 2f
        val sX = size.width / currentSize!!.width.toFloat()
        val sY = size.height / currentSize!!.height.toFloat()
        val millis = 15L
        val total = duration / millis.toInt()
        var cos: Float = 0f
        fun set() {
            alpha = cos
            translationX = dx * cos
            translationY = dy * cos
            scaleX = sX * cos + (1f - cos)
            scaleY = sY * cos + (1f - cos)
        }
        withContext(Dispatchers.Main){
            animate(initialValue = total.toFloat(), targetValue = 0f , animationSpec = tween(duration.toInt())) { v, _ ->
                val x = v / total.toFloat()
                cos = animationSpec(1 - x)
                set()
            }
        }
//        while (count >= 0) {
//            val x = count / total.toFloat()
//            cos = animationSpec(1 - x)
//            set()
//            count--
//            delay(millis)
//        }
        cos = 1f
        set()
        onEnd()
    }
    DisposableEffect(key1 = currentSize, key2 = expectedPosition) {
        if (currentSize != null && expectedPosition != null) {
            cancelableCoroutine.start {
                startAnimation()
            }
        }
        onDispose { }
    }
    DisposableEffect(key1 = finish.value) {
        if (finish.value) {
            cancelableCoroutine.start {
                finishAnimation(onFinishAnimEnd)
            }
        }
        onDispose {

        }
    }
    this
        .onGloballyPositioned {
            currentSize = it.size
            expectedPosition = it.positionInRoot()
        }
        .composed {
            if (scaleX != null && scaleY != null && translationX != null && translationY != null) {
                graphicsLayer {
                    this.translationY = translationY!!
                    this.translationX = translationX!!
                    this.scaleX = scaleX!!
                    this.scaleY = scaleY!!
                    onSetGraphicLayer(1 - alpha)
                }
            } else this.alpha(0f)
        }
}