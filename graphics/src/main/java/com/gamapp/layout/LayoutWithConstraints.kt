package com.gamapp.layout

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.unit.Constraints


interface ConstraintsScope {
    val constraints: State<Constraints>
}

class ConstraintsScopeImpl : ConstraintsScope {
    override val constraints: MutableState<Constraints> =
        mutableStateOf(Constraints.fixed(0, 0))

    @PublishedApi
    internal val readConstraints
        get() = constraints.value
}

@Composable
@PublishedApi
internal fun rememberConstraintsScopeMeasurePolicy(
    scopeImpl: ConstraintsScopeImpl,
    name: List<String>
) = remember {
    MeasurePolicy { measurables, constraints ->
        Log.i(TAG, "rememberConstraintsScopeMeasurePolicy: it measured $name ")
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        var width = 0
        var height = 0
        val placeables = measurables.map { it ->
            it.measure(childConstraints).also {
                width += it.width
                height += it.height
            }
        }
        width = width.coerceIn(constraints.minWidth, constraints.maxWidth)
        height = height.coerceIn(constraints.maxHeight, constraints.maxHeight)
        layout(width, height) {
            if (scopeImpl.readConstraints != constraints) {
                scopeImpl.constraints.value = constraints
            }
            placeables.forEach {
                it.place(0, 0)
            }
        }
    }
}

@Composable
inline fun LayoutWithConstraints(
    modifier: Modifier,
    content: @Composable ConstraintsScope.() -> Unit
) {
    val name = remember {
        Thread.currentThread().stackTrace.toList().map {
            it.methodName.toString()
        }
    }

    val scope = remember {
        ConstraintsScopeImpl()
    }
    val measurePolicy = rememberConstraintsScopeMeasurePolicy(scopeImpl = scope, name = name)
    Layout(
        content = {
            scope.content()
        },
        measurePolicy = measurePolicy,
        modifier = modifier
    )
}