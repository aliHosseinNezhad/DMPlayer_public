package com.gamapp.layout

import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.util.toHalf

interface FrameScope {
    fun Modifier.align(alignment2D: Alignment2D): Modifier
    fun Modifier.matchParentWidth(percent: Float = 1f): Modifier
    fun Modifier.matchParentHeight(percent: Float = 1f): Modifier
    fun Modifier.matchParentSize(percent: Float): Modifier {
        return matchParentWidth(percent).matchParentHeight(percent)
    }
}

internal object FrameScopeImpl : FrameScope {
    override fun Modifier.align(alignment2D: Alignment2D): Modifier =
        then(BoxLayoutAlignmentImpl(alignment = alignment2D))

    override fun Modifier.matchParentWidth(percent: Float): Modifier =
        then(BoxLayoutMatchParentImpl(matchParentWidth = percent))

    override fun Modifier.matchParentHeight(percent: Float): Modifier =
        then(BoxLayoutMatchParentImpl(matchParentHeight = percent))
}

object Alignment {
    object TopStart : Alignment2D(0f, 0f)
    object TopCenter : Alignment2D(0.5f, 0f)
    object TopEnd : Alignment2D(1f, 0f)

    object CenterStart : Alignment2D(0f, 0.5f)
    object Center : Alignment2D(0.5f, 0.5f)
    object CenterEnd : Alignment2D(1f, 0.5f)

    object BottomStart : Alignment2D(0f, 1f)
    object BottomCenter : Alignment2D(0.5f, 1f)
    object BottomEnd : Alignment2D(1f, 1f)
}


open class Alignment2D(
    @FloatRange(from = 0.0, to = 1.0) val x: Float,
    @FloatRange(from = 0.0, to = 1.0) val y: Float
) {
    override fun hashCode(): Int {
        return (x + 10 * (y + 1)).hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val alignment2d = other as? Alignment2D ?: return false
        return alignment2d.x == this.x && alignment2d.y == this.y
    }
}

class BoxParentData(
    var alignment: Alignment2D = Alignment2D(0f, 0f),
    var fillMaxWidth: Float? = null,
    var fillMaxHeight: Float? = null
)

@Composable
fun alignment(x: Float, y: Float) = remember(x, y) {
    Alignment2D(x, y)
}

internal class BoxLayoutAlignmentImpl(
    val alignment: Alignment2D
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) =
        ((parentData as? BoxParentData) ?: BoxParentData()).also {
            it.alignment = alignment
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherModifier = other as? BoxLayoutAlignmentImpl ?: return false
        return alignment == otherModifier.alignment
    }

    override fun hashCode(): Int {
        return alignment.hashCode()
    }

    override fun toString(): String =
        "BoxLayoutAlignment(${alignment.x},${alignment.y})"
}

internal class BoxLayoutMatchParentImpl(
    val matchParentWidth: Float? = null,
    val matchParentHeight: Float? = null
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) =
        ((parentData as? BoxParentData) ?: BoxParentData()).also {
            if (matchParentHeight != null)
                it.fillMaxHeight = matchParentHeight
            if (matchParentWidth != null)
                it.fillMaxWidth = matchParentWidth
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherModifier = other as? BoxLayoutMatchParentImpl ?: return false
        return otherModifier.matchParentHeight == matchParentHeight
                && otherModifier.matchParentWidth == matchParentWidth
    }

    override fun hashCode(): Int {
        val s1 = if (matchParentWidth == null) 100 else 0
        val s2 = if (matchParentHeight == null) 200 else 0
        val x = matchParentHeight ?: 0f
        val y = matchParentWidth ?: 0f
        return (x + 10 * (y + 1) + s1 + s2).hashCode()
    }
}

fun main() {
    println(BoxLayoutMatchParentImpl(null,null).hashCode())
    println(BoxLayoutMatchParentImpl(1f,null).hashCode())
    println(BoxLayoutMatchParentImpl(null,1f).hashCode())
    println(BoxLayoutMatchParentImpl(1f,1f).hashCode())
    println(BoxLayoutMatchParentImpl(0.1f,0.1f).hashCode())
    println(BoxLayoutMatchParentImpl(0.1f,0.8f).hashCode())
}

fun Placeable.PlacementScope.placeInFrame(
    placeable: Placeable,
    width: Int,
    height: Int,
    alignment: Alignment2D
) {
    val x = (width - placeable.width) * alignment.x
    val y = (height - placeable.height) * alignment.y
    placeable.place(x.toInt(), y.toInt())
}

@Composable
fun rememberBoxMeasurePolicy() = remember {
    MeasurePolicy { measurables, constraints ->
        if (measurables.isEmpty()) layout(constraints.minWidth, constraints.minHeight) {}
        val placeables = arrayOfNulls<Placeable>(measurables.size)
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val parentsData = measurables.map { it.parentData as? BoxParentData ?: BoxParentData() }

        var totalWidth = 0
        var totalHeight = 0
        measurables.fastForEachIndexed { i, measurable ->
            val data = parentsData[i]
            if (data.fillMaxWidth == null || data.fillMaxHeight == null) {
                placeables[i] = measurable.measure(childConstraints)
                if (data.fillMaxWidth == null)
                    totalWidth += placeables[i]!!.width
                if (data.fillMaxHeight == null)
                    totalHeight += placeables[i]!!.height
            }
        }
        val width = totalWidth.coerceIn(constraints.minWidth, constraints.maxWidth)
        val height = totalHeight.coerceIn(constraints.minHeight, constraints.maxHeight)
        measurables.fastForEachIndexed { i, measurable ->
            val data = parentsData[i]
            if (data.fillMaxWidth != null && data.fillMaxHeight != null) {
                placeables[i] = measurable.measure(
                    Constraints.fixed(
                        (width * data.fillMaxWidth!!).toInt(),
                        ((height * data.fillMaxHeight!!).toInt())
                    )
                )
            } else if (data.fillMaxWidth != null) {
                placeables[i] = measurable.measure(
                    Constraints.fixedWidth((width * data.fillMaxWidth!!).toInt())
                        .copy(minHeight = 0, maxHeight = constraints.maxHeight)
                )
            } else if (data.fillMaxHeight != null) {
                placeables[i] = measurable.measure(
                    Constraints.fixedHeight((height * data.fillMaxHeight!!).toInt())
                        .copy(minWidth = 0, maxWidth = constraints.maxWidth)
                )
            }
        }
        val out = placeables.filterNotNull()
        layout(width, height) {
            out.forEachIndexed { i, it ->
                placeInFrame(it, width, height, parentsData[i].alignment)
            }
        }
    }
}

@Composable
inline fun Frame(modifier: Modifier, crossinline content: @Composable FrameScope.() -> Unit) {
    val measurePolicy = rememberBoxMeasurePolicy()
    @Suppress("Deprecation")
    MultiMeasureLayout(measurePolicy = measurePolicy, content = {
        FrameScopeImpl.content()
    }, modifier = modifier)
}

