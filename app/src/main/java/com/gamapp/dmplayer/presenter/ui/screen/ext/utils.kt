package com.gamapp.dmplayer.presenter.ui.screen.ext

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

fun Modifier.surfaceTheme(elevation :Dp = 0.dp) = composed {
    graphicsLayer {
        shadowElevation = elevation.toPx()
        clip = true
        shape = RoundedCornerShape(25.dp)
    }.background(MaterialTheme.colors.surface)
}

fun Path.moveTo(offset: Offset) {
    moveTo(offset.x, offset.y)
}

fun Path.lineTo(offset: Offset) {
    lineTo(offset.x, offset.y)
}

class BottomRoundClip(private val radius: Dp = 25.dp) : Shape {

    private fun Path.moveTo(offset: Offset) {
        moveTo(offset.x, offset.y)
    }

    private fun Path.lineTo(offset: Offset) {
        lineTo(offset.x, offset.y)
    }

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val h = with(density) { size.height }
        val path = Path()
        val radius = with(density) { radius.toPx() }
        val start = Offset(0f, 0f)
        path.moveTo(start)
        path.lineTo(0f, h)
        path.lineTo(size.width, h)
        val arcLeftTopLeft = Offset(0f, h - radius)
        val arcLeftBottomRight = Offset(2 * radius, h + radius)
        path.arcTo(Rect(arcLeftTopLeft, arcLeftBottomRight), 180f, 90f, false)
        val arcRightTopLeft = Offset(size.width - 2 * radius, h - radius)
        path.lineTo(arcLeftTopLeft)
        val arcRightBottomRight = Offset(size.width, h + radius)
        path.arcTo(Rect(arcRightTopLeft, arcRightBottomRight), 270f, 90f, false)
        path.lineTo(size.width, 0f)
        path.close()
        return Outline.Generic(path)
    }
}

infix fun Float.offset(v: Float) = Offset(this, v)
class TopClip(private val radius: Dp = 25.dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val w = size.width
        val h = size.height
        val r = with(density) { radius.toPx() }
        val br = w offset h
        val bl = 0f offset h
        val tl = 0f offset 0f
        val tr = w offset 0f
        val cr1 = w - r offset 0f
        val cr2 = w offset r
        val cl2 = r offset r
        val cl1 = tl
        path.moveTo(br)
        path.lineTo(tr)
        val crRect = Rect(cr1, cr2)
        path.arcTo(
            rect = crRect,
            startAngleDegrees = 0f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        path.lineTo(cl2)
        val clRect = Rect(cl1, cl2)
        path.arcTo(
            rect = clRect,
            startAngleDegrees = 90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        path.lineTo(bl)
        path.close()
        return Outline.Generic(path)
    }

}