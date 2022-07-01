package com.gamapp.dmplayer.presenter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Preview
@Composable
fun Test() {
    var bytes by remember {
        mutableStateOf(listOf<Float>())
    }
    
    val context = LocalContext.current
//    LaunchedEffect(key1 = Unit) {
//        withContext(Dispatchers.IO) {
//            bytes = context.resources.assets.open("music.mp3").readBytes().map {
//                it / 256f
//            }
//        }
//    }
    LazyRow(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(
            bytes,
            key = { index, _ -> index },
            contentType = { _, _ -> "1" }) { _, item ->
            Spacer(modifier = Modifier.width(0.5f.dp))
            Element(color = Color.Blue, percent = item)
        }
    }
}


@Composable
fun Element(color: Color, percent: Float) {
    Spacer(
        modifier = Modifier
            .fillMaxHeight()
            .width(8.dp)
            .clip(verticalClip(percent = percent))
            .background(color)
    )
}

@Composable
fun verticalClip(percent: Float) = remember(percent) {
    VerticalClip(percent)
}

class VerticalClip(private val percent: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val r = size.toRect()
        val rect =
            Rect(
                left = r.left,
                right = r.right,
                bottom = r.bottom,
                top = r.bottom - r.height * percent.coerceIn(0f, 1f)
            )
        path.addRect(rect)
        return Outline.Generic(path)
    }
}


@Composable
fun Pazel() {
    Spacer(modifier = Modifier.width(1.dp))
}

