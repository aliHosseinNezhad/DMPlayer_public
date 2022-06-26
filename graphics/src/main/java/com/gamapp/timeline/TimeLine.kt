package com.gamapp.timeline

import android.widget.TextView
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.asLiveData
import com.gamapp.slider.Slider
import com.gamapp.slider.SliderColors
import com.gamapp.slider.SliderState
import java.util.concurrent.TimeUnit

fun Long.toTime(): String {
    return String.format(
        "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(this) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(this)),
        TimeUnit.MILLISECONDS.toSeconds(this) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this))
    );
}

@Composable
fun TimeLine(
    state: TimeLineState,
    modifier: Modifier,
    interactionSource: MutableInteractionSource = remember {
        MutableInteractionSource()
    },
    colors: SliderColors
) {
    Box(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .requiredHeight(60.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 10.dp)
                .height(20.dp)
        ) {
            AndroidText(text = remember {
                derivedStateOf {
                    state.current.value.toTime()
                }
            }, color = remember(colors) {
                mutableStateOf(colors.enabledThumbColor)
            },
                modifier = Modifier.height(20.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            AndroidText(text = remember {
                derivedStateOf {
                    state.duration.value.toTime()
                }
            }, color = remember(colors) {
                mutableStateOf(colors.enabledThumbColor)
            }, modifier = Modifier.height(20.dp))
        }
        Slider(
            state = state,
            modifier = Modifier.align(Alignment.TopCenter),
            interactionSource = interactionSource,
            colors = colors,

        )
    }
}

@Composable
fun AndroidText(
    text: State<String>,
    modifier: Modifier = Modifier,
    color: State<Color>,
    fontSize: State<TextUnit> = remember {
        mutableStateOf(12.sp)
    }
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(factory = { context ->
        TextView(context).apply {
            snapshotFlow { text.value }.asLiveData().observe(lifecycleOwner) {
                this.text = it
            }
            snapshotFlow { color.value }.asLiveData().observe(lifecycleOwner) { color ->
                this.setTextColor(color.toArgb())
            }
            snapshotFlow { fontSize.value }.asLiveData().observe(lifecycleOwner) { fontSize ->
                this.textSize = fontSize.value
            }
        }
    }, modifier = modifier)
}

class TimeLineState(
    value: State<Float>,
    val duration: State<Long>,
    val current: State<Long>,
    enabled: State<Boolean>,
    onValueChange: (Float) -> Unit,
    onFinallyValueChange: (Float) -> Unit = {}
) : SliderState(
    value = value,
    enabled = enabled,
    onValueChange = onValueChange,
    onFinallyValueChange = onFinallyValueChange
)