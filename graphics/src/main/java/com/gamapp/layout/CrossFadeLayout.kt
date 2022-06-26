package com.gamapp.layout

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun rememberCrossFadeLayoutMeasurePolicy(
    firstExist: State<Boolean>,
    secondExist: State<Boolean>,
    animator: Animatable<Float, AnimationVector1D>
) = remember {
    MeasurePolicy { measurables, constraints ->
        val childConstraints =
            constraints.copy(minWidth = constraints.maxWidth, minHeight = constraints.maxHeight)
        val first = measurables.find { it.layoutId == 1 }?.measure(childConstraints)
        val second = measurables.find { it.layoutId == 2 }?.measure(childConstraints)
        layout(constraints.maxWidth, constraints.maxHeight) {
            if (firstExist.value)
                first?.placeWithLayer(0, 0, layerBlock = {
                    this.alpha = animator.value
                })
            if (secondExist.value)
                second?.placeWithLayer(0, 0, layerBlock = {
                    this.alpha = 1 - animator.value
                })
        }
    }
}


@Composable
fun CrossFadeLayout(
    modifier: Modifier,
    one: @Composable BoxScope.() -> Unit,
    two: @Composable BoxScope.() -> Unit,
    animationSpec: FiniteAnimationSpec<Float>,
    showOne: State<Boolean>,
) {
    val animator = remember {
        Animatable(if (showOne.value) 1f else 0f)
    }
    LaunchedEffect(key1 = Unit) {
        snapshotFlow { showOne.value }.collect {
            animator.animateTo(if (it) 1f else 0f, animationSpec = animationSpec)
        }
    }
    val secondExist = remember {
        derivedStateOf {
            animator.value != 1f
        }
    }
    val firstExist = remember {
        derivedStateOf {
            animator.value != 0f
        }
    }

    Layout(
        measurePolicy = rememberCrossFadeLayoutMeasurePolicy(
            animator = animator,
            firstExist = firstExist,
            secondExist = secondExist
        ),
        modifier = modifier,
        content = {
            Box(modifier = Modifier.layoutId(1)) {
                if (firstExist.value)
                    one(this)
            }
            Box(modifier = Modifier.layoutId(2)) {
                if (secondExist.value)
                    two(this)
            }
        })
}


@Preview
@Composable
fun CrossFadeLayoutTest() {
    val show = remember {
        mutableStateOf(true)
    }
    androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                show.value = !show.value
            }, modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .wrapContentSize(align = Alignment.Center)
        ) {

        }
        CrossFadeLayout(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            one = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Blue)
                ) {
                    Text(
                        text = "first ${UUID.randomUUID()}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
            two = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Magenta)
                ) {
                    Text(
                        text = "second ${UUID.randomUUID()}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
            animationSpec = tween(500),
            showOne = show
        )
    }
}