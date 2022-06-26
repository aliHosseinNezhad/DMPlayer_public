package com.gamapp.dmplayer.presenter.ui.screen.listof.linear.albumList

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.dmplayer.presenter.ui.screen.elements.sort_elements.AlbumSortLayout
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.BaseListState
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.SortBarVisibility
import com.gamapp.dmplayer.presenter.viewmodel.AppViewModel
import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.sealedclasses.Sort
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
inline fun rememberAlbumState(set: AlbumsState.() -> Unit): AlbumsState {
    val context = LocalContext.current
    val viewModel = hiltViewModel<AppViewModel>()
    val state = remember(context, viewModel) {
        AlbumsState(context).apply(set)
    }
    return state
}

open class AlbumSortBarVisibility private constructor() : SortBarVisibility {
    class Visible(val sort: MutableStateFlow<Sort<AlbumModel>>) : AlbumSortBarVisibility()
    object Invisible : AlbumSortBarVisibility()
}

class AlbumsState(val context: Context) : BaseListState<AlbumModel>() {
    override var sortBarVisibility: AlbumSortBarVisibility by mutableStateOf(AlbumSortBarVisibility.Invisible)

    @Composable
    override fun SortBarContent(items: List<AlbumModel>) {
        if (sortBarVisibility is AlbumSortBarVisibility.Visible) {
            AlbumSortLayout(
                selection = selectionManager.onSelectionMode,
                albumSort = (sortBarVisibility as AlbumSortBarVisibility.Visible).sort
            )
        }
    }
}