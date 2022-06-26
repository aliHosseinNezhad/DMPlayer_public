package com.gamapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.gamapp.gesture.flingSwipeable
import com.gamapp.gesture.rememberFlingSwipeableState


@Composable
fun CollapsingTopBar() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val h = constraints.maxHeight
        val density = LocalDensity.current
        val scope = rememberCoroutineScope()
        val min = h * 0.2f
        val max = h * 0.9f
        val state = rememberFlingSwipeableState(min = min, max = max, initialValue = min)
        val height = with(density) { state.offset.value.toDp() }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(height)
                .flingSwipeable(state = state, reverseDirection = true)
                .background(Color.Blue))
    }
}