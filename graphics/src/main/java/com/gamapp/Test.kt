package com.gamapp

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.gamapp.custom.TimeLine
import kotlin.math.abs


@Composable
fun TestSeekBar() {
    var value by remember {
        mutableStateOf(0f)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    LaunchedEffect(key1 = interactionSource) {
        interactionSource.interactions.collect {
            val interaction = if (it is DragInteraction) when (it) {
                is DragInteraction.Start -> {
                    "drag/start"
                }
                is DragInteraction.Stop -> {
                    "drag/stop"
                }
                is DragInteraction.Cancel -> {
                    "drag/cancel"
                }
                else -> {
                    "drag/*"
                }
            }
            else if (it is PressInteraction) if (it is PressInteraction.Press) "press/press" else "press/cancel"
            else "none"
            Log.i(TAG, "TestSeekBar: $interaction")
        }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        TimeLine(
            value = { value },
            onValueChange = {

            },
            currentTime = { 0 },
            duration = { 5000 },
            interactionSource = interactionSource
        )
    }
}




const val max = 900f

@Preview
@Composable
fun NestedScrollTest() {
    var offset by remember {
        mutableStateOf(max)
    }
    fun scrollBy(ds: Float): Float {
        if (offset == 0f && ds < 0f) return 0.0f
        if (offset == max && ds > 0f) return 0.0f
        val pv = offset
        offset = (ds + offset).coerceIn(0f, max)
        return offset - pv
    }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return if (available.y < 0) {
                    val consumed = scrollBy(available.y)
                    Offset(x = 0f, y = consumed)
                } else Offset.Zero
            }
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return if (abs(available.y) > 0f &&
                    available.y > 0f
                ) {
                    Offset(0f, scrollBy(available.y))
                } else
                    super.onPostScroll(consumed, available, source)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                Log.i(TAG, "velocity:$available offset:$offset")
                return super.onPreFling(available)
            }

        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )
        LazyColumn(
            modifier = Modifier
                .graphicsLayer {
                    translationY = offset
                }
                .nestedScroll(connection = nestedScrollConnection)
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(100) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color.Blue)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
