package com.gamapp.dmplayer.presenter.ui.screen.elements

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextIcon(
    @DrawableRes icon: Int,
    text: String,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember {
        MutableInteractionSource()
    },
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .then(modifier)
            .graphicsLayer {
                clip = true
                shape = CircleShape
            }
            .background(MaterialTheme.colors.onSurface.copy(0.05f))
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(),
                onClick = onClick
            )
            .padding(horizontal = 8.dp)
            .wrapContentWidth()
            .height(30.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .wrapContentWidth()
                .height(60.dp)
                .wrapContentHeight(align = Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Light,
            fontSize = 15.sp,
            color = MaterialTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = rememberVectorPainter(image = ImageVector.vectorResource(icon)),
            contentDescription = "edit",
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.CenterVertically),
            tint = MaterialTheme.colors.onBackground
        )
    }
}