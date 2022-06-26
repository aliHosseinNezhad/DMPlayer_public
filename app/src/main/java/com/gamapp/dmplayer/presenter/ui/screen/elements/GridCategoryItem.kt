package com.gamapp.dmplayer.presenter.ui.screen.elements

import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.theme.light
import com.gamapp.dmplayer.presenter.ui.utils.NewLoadImage

@Composable
fun LazyGridItemScope.CategoryItem(
    id: Long,
    title: String,
    subtitle1: String,
    subtitle2: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .animateItemPlacement(tween(600))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(15),
            elevation = 4.dp,
            backgroundColor = Color.DarkGray,
            onClick = onClick
        ) {
            NewLoadImage(
                id = id,
                modifier = Modifier.fillMaxSize(),
                alpha = 1f,
                tint = Color.White,
                defaultImage = R.drawable.round_album_24
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 12.sp,
            color = if (isSystemInDarkTheme()) light else Color.DarkGray
        )
        CategorySubtitle(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp),
            subtitle1 = subtitle1,
            subtitle2 = subtitle2
        )
    }
}

@Composable
fun CategorySubtitle(
    modifier: Modifier,
    subtitle1: String,
    subtitle2: String
) {
    val colors = Color.Gray
    val measurePolicy = remember {
        MeasurePolicy { measurables, constraints ->
            val childConstraints = constraints.copy(minWidth = 0, minHeight = constraints.maxHeight)
            val two = measurables.firstOrNull { it.layoutId == "2" }!!.measure(childConstraints)
            val one = measurables.firstOrNull { it.layoutId == "1" }!!
                .measure(
                    Constraints.fixedHeight(constraints.maxHeight)
                        .copy(minWidth = 0, maxWidth = constraints.maxWidth - two.width)
                )
            val w = one.width + two.width
            layout(constraints.maxWidth, constraints.maxHeight) {
                val s = (constraints.maxWidth - w) / 2
                one.place(s, 0)
                two.place(s + one.width, 0)
            }
        }
    }
    Layout(measurePolicy = measurePolicy, content = {
        Text(
            modifier = Modifier.layoutId("1"),
            text = subtitle1,
            textAlign = TextAlign.End,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontSize = 10.sp,
            color = colors
        )
        Text(
            modifier = Modifier.layoutId("2"),
            text = subtitle2,
            textAlign = TextAlign.Start,
            maxLines = 1,
            fontSize = 10.sp,
            color = colors
        )
    }, modifier = modifier)

}