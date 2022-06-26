package com.gamapp.layout


import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.util.fastForEachIndexed

sealed class Weight {
    object Empty : Weight()
    class Amount(val value: Float) : Weight()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val o = other as? Weight ?: return false
        return if (o is Empty && this is Empty) true
        else if (o is Amount && this is Amount) {
            o.value == this.value
        } else false
    }

    override fun hashCode(): Int {
        return when (this) {
            is Empty -> super.hashCode()
            is Amount -> this.value.hashCode() * super.hashCode()
        }
    }
}

internal data class RowColumnParentData(
    var weight: Weight = Weight.Empty,
    var fill: Boolean = true,
    var alignment: Alignment1D = Alignment1D(0f)
)

open class Alignment1D(@FloatRange(from = 0.0, to = 1.0) val value: Float) {
    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val alignment = other as? Alignment1D ?: return false
        return value == alignment.value
    }
}

open class VerticalAlignment(@FloatRange(from = 0.0, to = 1.0) value: Float) : Alignment1D(value) {
    object Top : VerticalAlignment(0f)
    object Center : VerticalAlignment(0.5f)
    object Bottom : VerticalAlignment(1f)
}

open class HorizontalAlignment(@FloatRange(from = 0.0, to = 1.0) value: Float) :
    Alignment1D(value) {
    object Start : HorizontalAlignment(0f)
    object Center : HorizontalAlignment(0.5f)
    object End : HorizontalAlignment(1f)
}

@Composable
fun verticalAlignment(value: Float) = remember(value) {
    VerticalAlignment(value)
}

@Composable
fun horizontalAlignment(value: Float) = remember(value) {
    HorizontalAlignment(value)
}


interface RowScope {
    fun Modifier.weight(@FloatRange(from = 0.0) value: Float): Modifier
    fun Modifier.align(alignment: VerticalAlignment): Modifier
}

interface ColumnScope {
    fun Modifier.weight(@FloatRange(from = 0.0) value: Float): Modifier
    fun Modifier.align(alignment: HorizontalAlignment): Modifier
}

object RowScopeImpl : RowScope {
    override fun Modifier.weight(value: Float): Modifier {
        return this.then(LayoutWeightImpl(Weight.Amount(value)))
    }

    override fun Modifier.align(alignment: VerticalAlignment): Modifier {
        return this.then(
            LayoutAlignmentImpl(
                alignment = alignment
            )
        )
    }
}

object ColumnScopeImpl : ColumnScope {
    override fun Modifier.weight(value: Float): Modifier {
        return this.then(LayoutWeightImpl(Weight.Amount(value)))
    }

    override fun Modifier.align(alignment: HorizontalAlignment): Modifier {
        return this.then(
            LayoutAlignmentImpl(
                alignment = alignment
            )
        )
    }
}

internal class LayoutWeightImpl(
    val weight: Weight,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) =
        ((parentData as? RowColumnParentData) ?: RowColumnParentData()).also {
            it.weight = weight
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherModifier = other as? LayoutWeightImpl ?: return false
        return weight == otherModifier.weight
    }

    override fun hashCode(): Int {
        //        result = 31 * result + fill.hashCode()
        return weight.hashCode()
    }

    override fun toString(): String =
        "LayoutWeightImpl(weight=$weight)"
}

internal class LayoutAlignmentImpl(
    val alignment: Alignment1D
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) =
        ((parentData as? RowColumnParentData) ?: RowColumnParentData()).also {
            it.alignment = alignment
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LayoutAlignmentImpl) return false
        return alignment.value == other.alignment.value
    }

    override fun hashCode(): Int {
        return alignment.hashCode()
    }
}


fun Constraints.forWeight(
    isHorizontal: Boolean,
    totalWeight: Float,
    leftBound: Int,
    weight: Float
): Constraints {
    fun percent(): Float {
        return if (totalWeight == 0f) 0f else
            weight / totalWeight
    }
    return if (isHorizontal) Constraints.fixedWidth((leftBound * percent()).toInt())
        .copy(
            minHeight = 0,
            maxHeight = maxHeight
        )
    else Constraints.fixedHeight((leftBound * percent()).toInt())
        .copy(
            minWidth = 0,
            maxWidth = maxWidth
        )
}

fun Placeable.ax1Dimen(isHorizontal: Boolean) = if (isHorizontal) width else height
fun Placeable.ax2Dimen(isHorizontal: Boolean) = if (isHorizontal) height else width
internal fun Placeable.PlacementScope.placeIn(
    width: Int,
    height: Int,
    placeable: Placeable,
    ax1: Int,
    isHorizontal: Boolean,
    data: RowColumnParentData
) {
    val bound = if (isHorizontal) height else width
    val ax2 = ((bound - placeable.ax2Dimen(isHorizontal)) * data.alignment.value).toInt()
    if (isHorizontal) placeable.place(ax1, ax2)
    else placeable.place(ax2, ax1)
}

@Composable
fun rememberDRowMeasurePolicy(isHorizontal: Boolean = true) = remember(isHorizontal) {
    MeasurePolicy { measurables, constraints ->
        val childConstraints = constraints.copy(minHeight = 0, minWidth = 0)
        val placeables = arrayOfNulls<Placeable>(measurables.size)
        val childrenData =
            measurables.map { it.parentData as? RowColumnParentData ?: RowColumnParentData() }
        var totalWeight = 0f
        var totalBound = 0
        measurables.fastForEachIndexed { i, measurable ->
            val m = childrenData[i]
            if (m.weight is Weight.Empty) {
                placeables[i] = measurable.measure(childConstraints).also {
                    totalBound += it.ax1Dimen(isHorizontal)
                }
            } else {
                val value = (m.weight as Weight.Amount).value
                totalWeight += value
            }
        }
        val leftBound = totalBound.let {
            if (isHorizontal) constraints.maxWidth - it.coerceIn(0, constraints.maxWidth)
            else constraints.maxHeight - it.coerceIn(0, constraints.maxHeight)
        }.coerceAtLeast(0)
        if (leftBound != 0 && totalWeight != 0f)
            measurables.fastForEachIndexed { i, measurable ->
                val m = childrenData[i]
                if (m.weight is Weight.Amount) {
                    val c = constraints.forWeight(
                        isHorizontal = isHorizontal,
                        totalWeight = totalWeight,
                        leftBound = leftBound,
                        weight = (m.weight as Weight.Amount).value
                    )
                    placeables[i] = measurable.measure(c)
                }
            }
        val outPlaceable = placeables.filterNotNull()
        val width = if (!constraints.hasFixedWidth)
            outPlaceable.maxOf { it.width }.coerceIn(constraints.minWidth, constraints.maxWidth)
        else constraints.minWidth
        val height = if (!constraints.hasFixedHeight)
            outPlaceable.maxOf { it.height }.coerceIn(constraints.minHeight, constraints.maxHeight)
        else constraints.minHeight
        layout(width, height) {
            var v = 0
            outPlaceable.forEachIndexed { i, placeable ->
                placeIn(
                    width = width,
                    height = height,
                    placeable = placeable,
                    ax1 = v,
                    isHorizontal = isHorizontal,
                    data = childrenData[i]
                )
                v += placeable.ax1Dimen(isHorizontal)
            }
        }
    }
}

@Composable
inline fun Row(modifier: Modifier, content: @Composable RowScope.() -> Unit) {
    val measurePolicy = rememberDRowMeasurePolicy(isHorizontal = true)
    Layout(measurePolicy = measurePolicy, content = {
        RowScopeImpl.content()
    }, modifier = modifier)
}

@Composable
inline fun Column(modifier: Modifier, content: @Composable() (ColumnScope.() -> Unit)) {
    val measurePolicy = rememberDRowMeasurePolicy(isHorizontal = false)
    Layout(measurePolicy = measurePolicy, content = {
        ColumnScopeImpl.content()
    }, modifier = modifier)
}
//
//@Preview
//@Composable
//fun Test() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .scale(1f)
//            .background(Color.Cyan.copy(0.2f))
//    ) {
//        Box(
//            modifier = Modifier
//                .weight(1f)
//                .width(100.dp)
//                .background(Color.Blue)
//                .align(horizontalAlignment(value = 0.8f))
//        )
//        Box(
//            modifier = Modifier
//                .weight(1f)
//                .width(200.dp)
//                .background(Color.Red)
//                .align(HorizontalAlignment.Center)
//        )
//    }
//}