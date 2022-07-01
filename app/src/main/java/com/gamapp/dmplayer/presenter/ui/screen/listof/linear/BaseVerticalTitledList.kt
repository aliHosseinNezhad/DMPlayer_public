package com.gamapp.dmplayer.presenter.ui.screen.listof.linear

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.gamapp.dmplayer.presenter.TopBarType
import com.gamapp.dmplayer.presenter.ui.screen.topbar.TitleBar
import com.gamapp.dmplayer.presenter.ui.screen.topbar.SelectionBar
import com.gamapp.dmplayer.presenter.utils.SelectionManager
import com.gamapp.dmplayer.presenter.utils.navigationBarHeight
import com.gamapp.dmplayer.presenter.utils.statusBarHeight
import com.gamapp.domain.models.ImagedItemModel
import com.gamapp.layout.CrossFadeLayout

@Composable
fun <T : ImagedItemModel> topBarType(
    items: List<T>,
    selectionManager: SelectionManager<T>
): State<TopBarType> {
    val tracksState = rememberUpdatedState(newValue = items)
    val topBarType = remember {
        mutableStateOf<TopBarType>(TopBarType.None)
    }
    val selectionTopBar = remember {
        TopBarType.Selection(checkbox = { checked ->
            if (checked) {
                selectionManager.addAll(tracksState.value)
            } else selectionManager.eraseAll()
        }, isChecked = selectionManager.isAllSelected)
    }
    val onSelectionMode = selectionManager.onSelectionMode
    LaunchedEffect(key1 = onSelectionMode) {
        topBarType.value = if (onSelectionMode) selectionTopBar else TopBarType.None
    }
    return topBarType
}


@Composable fun <T : ImagedItemModel> BaseVerticalTitledList(
    title: String,
    items: List<T>,
    state: BaseListState<T>,
    modifier: Modifier,
    popupList: List<Pair<String, (T) -> Unit>> = emptyList(),
    onEmptyListImage: @Composable BoxScope.() -> Unit,
    onItemClicked: (T) -> Unit,
    other:  @Composable BoxScope.() -> Unit,
    popBack: () -> Unit
) {
    val topBarType by topBarType(items = items, selectionManager = state.selectionManager)
    val showOne = remember {
        derivedStateOf {
            topBarType !is TopBarType.Selection
        }
    }
    Column(
        modifier = Modifier
            .then(modifier)
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .padding(top = statusBarHeight())
            .padding(bottom = navigationBarHeight() + 15.dp)
            .clip(RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp))
    ) {
        CrossFadeLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            one = {
                TitleBar(title = title, onBackPress = popBack)
            },
            two = {
                if (topBarType is TopBarType.Selection)
                    SelectionBar(type = topBarType as TopBarType.Selection)
            },
            animationSpec = tween(500),
            showOne = showOne
        )
        BaseList(
            items = items,
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(shape = RoundedCornerShape(25.dp))
                .background(MaterialTheme.colors.surface),
            onItemClicked = onItemClicked,
            popupList = popupList,
            other = other,
            emptyContent = onEmptyListImage,
        )
    }
}