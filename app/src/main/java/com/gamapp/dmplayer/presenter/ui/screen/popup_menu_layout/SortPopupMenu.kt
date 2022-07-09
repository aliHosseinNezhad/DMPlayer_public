package com.gamapp.dmplayer.presenter.ui.screen.popup_menu_layout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.theme.primary
import com.gamapp.domain.models.Image
import com.gamapp.domain.sealedclasses.Sort

@Composable
fun SortItem(name: String, onSelect: () -> Unit, selected: Boolean) {
    Row(
        modifier = Modifier
            .clickable(onClick = onSelect)
            .padding(horizontal = 12.dp)
            .height(50.dp)
    ) {
        Text(
            text = name,
            fontSize = 18.sp,
            modifier = Modifier
                .align(CenterVertically),
            maxLines = 1,
            color = if (selected) primary else MaterialTheme.colors.onSurface,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (selected)
            Icon(
                painter = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.ic_check)),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .align(CenterVertically),
                tint = primary
            )
    }
}

@Composable
internal fun rememberSortPopupMeasurePolicy() = remember {
    MeasurePolicy { measureables, constraints ->
        val w = measureables.maxOf { it.maxIntrinsicWidth(0.dp.roundToPx()) }
        val p = measureables.map {
            it.measure(
                Constraints(
                    minWidth = w,
                    maxWidth = w,
                    minHeight = 0,
                    maxHeight = constraints.maxHeight
                )
            )
        }
        var h = 0
        layout(
            p.maxOf { it.width }.coerceIn(100.dp.roundToPx(), 400.dp.roundToPx()),
            p.sumOf { it.height }.coerceAtMost(500.dp.roundToPx())
        ) {
            p.forEach {
                it.placeRelative(0, h)
                h += it.height
            }
        }
    }
}

@Composable
fun <T: Image> SortPopupMenu(
    show: MutableState<Boolean>,
    items: List<Sort.Type<T>>,
    onSelect: Sort.Type<T>.() -> Unit,
    selected: Sort.Type<T>?
) {
    val selectedIndex = items.indexOfFirst {
        it == selected
    }
    val measurePolicy = rememberSortPopupMeasurePolicy()
    val state = rememberScrollState()
    PopupMenu(show = show) {
        Layout(
            measurePolicy = measurePolicy, content = {
                Text(
                    text = "Sort by :",
                    maxLines = 1,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .height(30.dp)
                        .wrapContentHeight(align = CenterVertically),
                    color = MaterialTheme.colors.onSurface.copy(0.5f),
                    textAlign = TextAlign.Start
                )
                items.forEachIndexed { index, it ->
                    SortItem(
                        name = it.name,
                        onSelect = {
                            onSelect(it)
                            exit()
                        }, selected = index == selectedIndex
                    )
                }
            }, modifier = Modifier
                .verticalScroll(state = state)
        )
    }
}