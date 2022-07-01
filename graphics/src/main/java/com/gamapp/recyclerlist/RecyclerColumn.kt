package com.gamapp.recyclerlist

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gamapp.layout.Column


class Item(
    val index: MutableState<Int> = mutableStateOf(0),
    val i: MutableState<Int> = mutableStateOf(0)
) {
    constructor(index: Int) : this(mutableStateOf(index))
    constructor(index: Int, i: Int) : this(mutableStateOf(index), mutableStateOf(i))

    var placeable: Placeable? = null
}
//
//@Preview
//@Composable
//fun RecyclerColumnPreview() {
//    RecyclerColumn(count = 10, content = { text ->
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp)
//                .height(50.dp)
//                .scrollable(
//                    state = rememberScrollableState { it },
//                    orientation = Orientation.Vertical
//                )
//        ) {
//            Text(text = text.value.toString())
//        }
//    })
//
//}


@Composable
inline fun Content(data: State<Int>, content: @Composable (State<Int>) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()

    ) {
        content(data)
    }
}

@Composable
fun RecyclerColumn(count: Int, content: @Composable (State<Int>) -> Unit) {
    val state = remember {
        RecyclerColumnState()
    }
    SubcomposeLayout(measurePolicy = remember {
        {
            val constraints = it.copy(minWidth = 0, minHeight = 0)
            var t = 0
            val temp = mutableMapOf<Int, Item>()
            for (i in 0 until count) {
                val item = state.currentItems.getOrPut(i) {
                    Item(i)
                }
                temp[i] = item
                val m = subcompose(i) {
                    Content(data = item.index, content)
                }.first()
                val p = m.measure(constraints)
                item.placeable = p
                t += p.height
                if (t > constraints.maxHeight) {
                    state.currentItems.clear()
                    state.currentItems.putAll(temp)
                    break
                }
            }
            layout(constraints.maxWidth, constraints.maxHeight) {
                var offset = 0
                state.currentItems.forEach { entry ->
                    entry.value.placeable?.let { placeable ->
                        placeable.place(0, offset)
                        offset += placeable.height
                    }
                }
            }
        }
    })
}


class RecyclerColumnState {
    val currentItems = mutableMapOf<Int, Item>()
}