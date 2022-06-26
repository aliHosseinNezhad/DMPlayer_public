package com.gamapp.gesture

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.cancellation.CancellationException

inline fun <T> Iterable<T>.sumOf(selector: (T) -> Float): Float {
    var sum = 0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

class VelocityTracker {
    val data = LinkedList<Pair<Float, Long>>()
    fun add(drag: Float, upMillis: Long) {
        data += drag to upMillis
        if (data.size > 8) {
            data.removeFirst()
        }
    }

    fun velocity(): Float {
        if (data.size < 2) return 0f
        val start = data.first.second
        val end = data.last.second
        val duration = end - start
        if (duration < 0) return 0f
        val movement = data.sumOf { it.first } * 1000
        return movement / duration
    }

    fun reset() {
        data.clear()
    }
}
fun DraggableState(onDelta: (Float) -> Unit): DraggableState = DefaultDraggableState(onDelta)
@PublishedApi internal class IgnorePointerDraggableState(val origin: DraggableState) : PointerAwareDraggableState,
    PointerAwareDragScope {
    var latestConsumptionScope: DragScope? = null

    override fun dragBy(pixels: Float, pointerPosition: Offset) {
        latestConsumptionScope?.dragBy(pixels)
    }

    override suspend fun drag(
        dragPriority: MutatePriority,
        block: suspend PointerAwareDragScope.() -> Unit
    ) {
        origin.drag(dragPriority) {
            latestConsumptionScope = this
            block()
        }
    }

    override fun dispatchRawDelta(delta: Float) {
        origin.dispatchRawDelta(delta)
    }
}
internal class DefaultDraggableState(val onDelta: (Float) -> Unit) : DraggableState {
    private val dragScope: DragScope = object : DragScope {
        override fun dragBy(pixels: Float): Unit = onDelta(pixels)
    }

    private val scrollMutex = MutatorMutex()

    override suspend fun drag(
        dragPriority: MutatePriority,
        block: suspend DragScope.() -> Unit
    ): Unit = coroutineScope {
        scrollMutex.mutateWith(dragScope, dragPriority, block)
    }

    override fun dispatchRawDelta(delta: Float) {
        return onDelta(delta)
    }
}
interface PointerAwareDragScope {
    fun dragBy(pixels: Float, pointerPosition: Offset): Unit
}

interface PointerAwareDraggableState {
    suspend fun drag(
        dragPriority: MutatePriority = MutatePriority.Default,
        block: suspend PointerAwareDragScope.() -> Unit
    )

    fun dispatchRawDelta(delta: Float)
}

private sealed class DragEvent {
    class DragStarted(val startPoint: Offset) : DragEvent()
    class DragStopped(val velocity: Float) : DragEvent()
    object DragCancelled : DragEvent()
    class DragDelta(val delta: Float, val pointerPosition: Offset) : DragEvent()
}

private class DragLogic(
    val onDragStarted: suspend CoroutineScope.(startedPosition: Offset) -> Unit,
    val onDragStopped: suspend CoroutineScope.(velocity: Float) -> Unit,
    val dragStartInteraction: MutableState<DragInteraction.Start?>,
    val interactionSource: MutableInteractionSource?
) {
    suspend fun CoroutineScope.processDragStart(event: DragEvent.DragStarted) {
        dragStartInteraction.value?.let { oldInteraction ->
            interactionSource?.emit(DragInteraction.Cancel(oldInteraction))
        }
        val interaction = DragInteraction.Start()
        interactionSource?.emit(interaction)
        dragStartInteraction.value = interaction
        onDragStarted.invoke(this, event.startPoint)
    }

    suspend fun CoroutineScope.processDragStop(event: DragEvent.DragStopped) {
        dragStartInteraction.value?.let { interaction ->
            interactionSource?.emit(DragInteraction.Stop(interaction))
            dragStartInteraction.value = null
        }
        onDragStopped.invoke(this, event.velocity)
    }

    suspend fun CoroutineScope.processDragCancel() {
        dragStartInteraction.value?.let { interaction ->
            interactionSource?.emit(DragInteraction.Cancel(interaction))
            dragStartInteraction.value = null
        }
        onDragStopped.invoke(this, 0f)
    }
}

fun Modifier.draggable(
    stateFactory: @Composable () -> PointerAwareDraggableState,
    orientation: Orientation,
    canDrag: (PointerInputChange) -> Boolean = { true },
    enabled: Boolean = true,
    reverseDirection: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
    onDragStopped: suspend CoroutineScope.(velocity: Float) -> Unit = {},
    onDragStarted: suspend CoroutineScope.(Offset) -> Unit = {},
) = composed {
    val state = stateFactory.invoke()
    val draggedInteraction = remember { mutableStateOf<DragInteraction.Start?>(null) }
    DisposableEffect(interactionSource) {
        onDispose {
            draggedInteraction.value?.let { interaction ->
                interactionSource?.tryEmit(DragInteraction.Cancel(interaction))
                draggedInteraction.value = null
            }
        }
    }
    val channel = remember { Channel<DragEvent>(capacity = Channel.UNLIMITED) }
    val canDragState = rememberUpdatedState(canDrag)
    val dragLogic by rememberUpdatedState(
        DragLogic(onDragStarted, onDragStopped, draggedInteraction, interactionSource)
    )
    LaunchedEffect(state) {
        while (isActive) {
            var event = channel.receive()
            if (event !is DragEvent.DragStarted) continue
            with(dragLogic) { processDragStart(event as DragEvent.DragStarted) }
            try {
                state.drag(MutatePriority.UserInput) {
                    while (event !is DragEvent.DragStopped && event !is DragEvent.DragCancelled) {
                        (event as? DragEvent.DragDelta)?.let {
                            dragBy(it.delta, it.pointerPosition)
                        }
                        event = channel.receive()
                    }
                }
                with(dragLogic) {
                    if (event is DragEvent.DragStopped) {
                        processDragStop(event as DragEvent.DragStopped)
                    } else if (event is DragEvent.DragCancelled) {
                        processDragCancel()
                    }
                }
            } catch (c: CancellationException) {
                with(dragLogic) { processDragCancel() }
            }
        }
    }
    Modifier.pointerInput(orientation, enabled, reverseDirection) {
        if (!enabled) return@pointerInput
        val tracker = VelocityTracker()
        detectDragGestures(
            orientation = orientation,
            reverseDirection = reverseDirection,
            onDragEnd = {
                val velocity = tracker.velocity()
                tracker.reset()
                channel.trySend(DragEvent.DragStopped(velocity))
            },
            onDragCanceled = {
                tracker.reset()
                channel.trySend(DragEvent.DragCancelled)
            },
            onDragStart = {
                tracker.reset()
                channel.trySend(DragEvent.DragStarted(it))
            },
        ) { change, dragAmount ->
            if (canDragState.value.invoke(change)) {
                tracker.add(drag = dragAmount, upMillis = change.uptimeMillis)
                channel.trySend(DragEvent.DragDelta(dragAmount,change.position))
            }
        }
    }
}


suspend fun PointerInputScope.detectDragGestures(
    orientation: Orientation,
    reverseDirection: Boolean,
    onDragStart: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCanceled: () -> Unit = {},
    onDragChanged: (change: PointerInputChange, dragAmount: Float) -> Unit
) {
    val sign = if (reverseDirection) -1f else 1f
    if (orientation == Orientation.Vertical) {
        detectVerticalDragGestures(
            onDragStart = onDragStart,
            onDragEnd = onDragEnd,
            onDragCancel = onDragCanceled,
            onVerticalDrag = { change, dragAmount ->
                onDragChanged(change, sign * dragAmount)
            }
        )
    } else detectHorizontalDragGestures(
        onDragStart = onDragStart,
        onDragEnd = onDragEnd,
        onDragCancel = onDragCanceled,
        onHorizontalDrag = { change, dragAmount ->
            onDragChanged(change, sign * dragAmount)
        }
    )
}