package com.gamapp.dmplayer.presenter.ui.screen.listof.linear

import android.content.Context
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gamapp.dmplayer.broadcast
import com.gamapp.dmplayer.presenter.TopBarType
import com.gamapp.dmplayer.presenter.ui.screen.popup_menu_layout.TextPopupMenu
import com.gamapp.dmplayer.presenter.ui.screen.elements.VerticalListItem
import com.gamapp.dmplayer.presenter.ui.screen.elements.rememberBaseItem
import com.gamapp.dmplayer.presenter.utils.SelectionManager
import com.gamapp.domain.ACTIONS
import com.gamapp.domain.models.ImagedItemModel
import kotlinx.coroutines.flow.MutableStateFlow


interface SortBarVisibility

abstract class BaseListState<T : ImagedItemModel> {
    val selectionManager: SelectionManager<T> = SelectionManager()
    abstract val sortBarVisibility: SortBarVisibility
    var canHideTrackPlayer by mutableStateOf(true)

    @PublishedApi
    internal var lazyListState by mutableStateOf(LazyListState())

    @Composable
    abstract fun SortBarContent(items: List<T>)

    @Composable
    fun Init(items: List<T>) {
        selectionManager.Init(items = items)
        val onSelection = selectionManager.onSelectionMode
        val context = LocalContext.current
        LaunchedEffect(key1 = onSelection) {
            val action = if (onSelection) {
                ACTIONS.PLAYER.HIDE
            } else ACTIONS.PLAYER.SHOW
            context.changePlayerVisibility(action)
        }
    }

    private fun Context.changePlayerVisibility(action: String) {
        if (canHideTrackPlayer)
            broadcast(action)
    }

    @Composable
    fun SelectionLaunchedScope(tracks: List<T>, topBarType: MutableStateFlow<TopBarType>) {
        val tracksState = rememberUpdatedState(newValue = tracks)
        val selectionType = remember {
            TopBarType.Selection(checkbox = { checked ->
                if (checked) {
                    selectionManager.addAll(tracksState.value)
                } else selectionManager.eraseAll()
            }, isChecked = selectionManager.isAllSelected)
        }
        val onSelectionManager = selectionManager.onSelectionMode
        LaunchedEffect(key1 = onSelectionManager) {
            topBarType.emit(if (onSelectionManager) selectionType else TopBarType.None)
        }
    }
}

@Composable
inline fun <T : ImagedItemModel> BaseList(
    items: List<T>,
    state: BaseListState<T>,
    modifier: Modifier,
    popupList: List<Pair<String, (T) -> Unit>> = emptyList(),
    emptyContent: @Composable BoxScope.() -> Unit = {},
    noinline isFocused: @Composable (T) -> Boolean = { false },
    noinline onItemClicked: (T) -> Unit,
    other: @Composable BoxScope.() -> Unit
) {
    state.Init(items = items)
    Box(modifier = modifier) {
        when {
            items.isEmpty() -> {
                emptyContent()
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = rememberLazyListState()
                ) {
                    stickyHeader(contentType = "header") {
                        state.SortBarContent(items = items)
                    }
                    items(items, key = { item ->
                        item.id
                    }, contentType = {
                        "item"
                    }) { item ->
                        val baseItem = rememberBaseItem(input = item)
                        VerticalListItem(
                            modifier = Modifier.composed {
                                if (items.size < 10) animateItemPlacement(tween(600)) else this
                            },
                            item = baseItem,
                            onClick = {
                                if (state.selectionManager.onSelectionMode) {
                                    state.selectionManager.onClick(item)
                                } else {
                                    onItemClicked(item)
                                }
                            },
                            onLongClick = {
                                state.selectionManager.onLongClick(item)
                            },
//                            popupContent = { it ->
//                                TextPopupMenu(show = it.value, popupList = remember(popupList) {
//                                    popupList.map {
//                                        it.first to {
//                                            it.second(item)
//                                        }
//                                    }
//                                }, onDismiss = {
//                                    it.value = false
//                                })
//                            },
                            onSelection = state.selectionManager.onSelectionMode
                        )
                    }
                    item(contentType = "spacer") {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
                other()
            }
        }
    }
}


