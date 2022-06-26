package com.gamapp.switch

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp


@Composable
fun Switch(
    modifier: Modifier,
    selectedColor: Color,
    border: Color,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val transition = updateTransition(targetState = isChecked, label = null)
    val value by transition.animateFloat(transitionSpec = { spring() }, label = "value") {
        if (it) 1f else 0f
    }
    Box(
        modifier = Modifier
            .then(modifier)
            .aspectRatio(1f)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = rememberRipple(color = selectedColor)
            ) {
                onCheckedChange(!isChecked)
            }
    ) {
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .alpha(1 - value)
                .border(
                    2.dp,
                    color = border,
                    shape = CircleShape
                )
        )
        Box(modifier = Modifier
            .matchParentSize()
            .graphicsLayer {
                alpha = value
                scaleY = value
                scaleX = value
                shape = CircleShape
                clip = true
            }
            .background(selectedColor)) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        scaleY = 0.6f
                        scaleX = 0.6f
                    },
                tint = Color.White
            )
        }
    }
}