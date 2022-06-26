package com.gamapp

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastFirstOrNull
import kotlin.math.abs
import kotlin.math.sign

private val mouseSlop = 0.125.dp
private val defaultTouchSlop = 18.dp // The default touch slop on Android devices
private val mouseToTouchSlopRatio = mouseSlop / defaultTouchSlop
fun ViewConfiguration.pointerSlop(pointerType: PointerType): Float {
    return when (pointerType) {
        PointerType.Mouse -> touchSlop * mouseToTouchSlopRatio
        else -> touchSlop
    }
}

fun PointerEvent.isPointerUp(pointerId: PointerId): Boolean =
    changes.fastFirstOrNull { it.id == pointerId }?.pressed != true

suspend fun AwaitPointerEventScope.awaitHorizontalPointerSlopOrCancellation(
    pointerId: PointerId,
    pointerType: PointerType,
    onPointerSlopReached: (change: PointerInputChange, overSlop: Float) -> Unit
) = awaitPointerSlopOrCancellation(
    pointerId = pointerId,
    pointerType = pointerType,
    onPointerSlopReached = onPointerSlopReached,
    getDragDirectionValue = { it.x }
)

suspend inline fun AwaitPointerEventScope.awaitPointerSlopOrCancellation(
    pointerId: PointerId,
    pointerType: PointerType,
    onPointerSlopReached: (PointerInputChange, Float) -> Unit,
    getDragDirectionValue: (Offset) -> Float
): PointerInputChange? {
    if (currentEvent.isPointerUp(pointerId)) {
        return null // The pointer has already been lifted, so the gesture is canceled
    }
    val touchSlop = viewConfiguration.pointerSlop(pointerType)
    var pointer: PointerId = pointerId
    var totalPositionChange = 0f

    while (true) {
        val event = awaitPointerEvent()
        val dragEvent = event.changes.fastFirstOrNull { it.id == pointer }!!
        if (dragEvent.positionChangeConsumed()) {
            return null
        } else if (dragEvent.changedToUpIgnoreConsumed()) {
            val otherDown = event.changes.fastFirstOrNull { it.pressed }
            if (otherDown == null) {
                // This is the last "up"
                return null
            } else {
                pointer = otherDown.id
            }
        } else {
            val currentPosition = dragEvent.position
            val previousPosition = dragEvent.previousPosition
            val positionChange = getDragDirectionValue(currentPosition) -
                    getDragDirectionValue(previousPosition)
            totalPositionChange += positionChange

            val inDirection = abs(totalPositionChange)
            if (inDirection < touchSlop) {
                // verify that nothing else consumed the drag event
                awaitPointerEvent(PointerEventPass.Final)
                if (dragEvent.positionChangeConsumed()) {
                    return null
                }
            } else {
                onPointerSlopReached(
                    dragEvent,
                    totalPositionChange - (sign(totalPositionChange) * touchSlop)
                )
                if (dragEvent.positionChangeConsumed()) {
                    return dragEvent
                } else {
                    totalPositionChange = 0f
                }
            }
        }
    }
}

suspend fun AwaitPointerEventScope.awaitVerticalPointerSlopOrCancellation(
    pointerId: PointerId,
    pointerType: PointerType,
    onTouchSlopReached: (change: PointerInputChange, overSlop: Float) -> Unit
) = awaitPointerSlopOrCancellation(
    pointerId = pointerId,
    pointerType = pointerType,
    onPointerSlopReached = onTouchSlopReached,
    getDragDirectionValue = { it.y }
)

suspend fun AwaitPointerEventScope.awaitFirstDownOnPass(
    pass: PointerEventPass,
    requireUnconsumed: Boolean
): PointerInputChange {
    var event: PointerEvent
    do {
        event = awaitPointerEvent(pass)
    } while (
        !event.changes.fastAll {
            if (requireUnconsumed) it.changedToDown() else it.changedToDownIgnoreConsumed()
        }
    )
    return event.changes[0]
}

suspend fun AwaitPointerEventScope.awaitDownAndSlop(
    canDrag: State<(PointerInputChange) -> Boolean>,
    startDragImmediately: State<() -> Boolean>,
    velocityTracker: VelocityTracker,
    orientation: Orientation
): Pair<PointerInputChange, Float>? {
    val initialDown =
        awaitFirstDownOnPass(requireUnconsumed = false, pass = PointerEventPass.Initial)
    return if (!canDrag.value.invoke(initialDown)) {
        null
    } else if (startDragImmediately.value.invoke()) {
        initialDown.consumeAllChanges()
        velocityTracker.addPointerInputChange(initialDown)
        // since we start immediately we don't wait for slop and the initial delta is 0
        initialDown to 0f
    } else {
        val down = awaitFirstDown(requireUnconsumed = false)
        velocityTracker.addPointerInputChange(down)
        var initialDelta = 0f
        val postPointerSlop = { event: PointerInputChange, offset: Float ->
            velocityTracker.addPointerInputChange(event)
            event.consumePositionChange()
            initialDelta = offset
        }
        val afterSlopResult = if (orientation == Orientation.Vertical) {
            awaitVerticalPointerSlopOrCancellation(down.id, down.type, postPointerSlop)
        } else {
            awaitHorizontalPointerSlopOrCancellation(down.id, down.type, postPointerSlop)
        }
        if (afterSlopResult != null) afterSlopResult to initialDelta else null
    }
}