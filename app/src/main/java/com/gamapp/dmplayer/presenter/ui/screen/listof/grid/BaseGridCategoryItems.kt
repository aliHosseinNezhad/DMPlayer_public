package com.gamapp.dmplayer.presenter.ui.screen.listof.grid

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
inline fun <T> BaseGridCategoryItems(
    TopItem: @Composable BoxScope.() -> Unit,
    crossinline item: @Composable LazyGridItemScope.(T) -> Unit,
    emptyListImage: @Composable BoxScope.() -> Unit = {},
    items: List<T>,
    crossinline key: (T) -> Any,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (items.isEmpty()) {
            true -> {
                emptyListImage()
            }
            false -> {
                BaseCategoryItemsWithNotEmptyList(
                    TopItem = TopItem,
                    item = item,
                    items = items,
                    key = key
                )
            }
        }
    }
}

@Composable
inline fun <T> BoxScope.BaseCategoryItemsWithNotEmptyList(
    TopItem: @Composable BoxScope.() -> Unit,
    crossinline item: @Composable LazyGridItemScope.(T) -> Unit,
    items: List<T>,
    crossinline key: (T) -> Any,
) {
    TopItem.invoke(this)
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), content = {
            items(items, key = {
                key(it)
            }, contentType = {
                "item"
            }) {
                item(it)
            }
            item(span = {
                GridItemSpan(2)
            }) {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }, modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
            .padding(horizontal = 4.dp),
        state = rememberLazyGridState()
    )
}
