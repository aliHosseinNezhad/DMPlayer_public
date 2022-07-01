package com.gamapp.dmplayer.presenter.ui.screen.listof.artist

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.dmplayer.presenter.ui.screen.elements.sort_elements.ArtistSortLayout
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.BaseListState
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.SortBarVisibility
import com.gamapp.dmplayer.presenter.viewmodel.AppViewModel
import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.sealedclasses.Sort
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
inline fun rememberArtistState(set: ArtistsState.() -> Unit): ArtistsState {
    val context = LocalContext.current
    val viewModel = hiltViewModel<AppViewModel>()
    val state = remember(context, viewModel) {
        ArtistsState(context).apply(set)
    }
    return state
}
open class ArtistSortBarVisibility private constructor() : SortBarVisibility {
    class Visible(val sort: MutableStateFlow<Sort<ArtistModel>>) : ArtistSortBarVisibility()
    object Invisible : ArtistSortBarVisibility()
}

class ArtistsState (private val context:Context): BaseListState<ArtistModel>() {
    override var sortBarVisibility: ArtistSortBarVisibility by mutableStateOf(
        ArtistSortBarVisibility.Invisible
    )

    @Composable
    override fun SortBarContent(items: List<ArtistModel>) {
        if (sortBarVisibility is ArtistSortBarVisibility.Visible){
            ArtistSortLayout(selection = selectionManager.onSelectionMode,
                (sortBarVisibility as ArtistSortBarVisibility.Visible).sort)
        }
    }

    @Composable
    override fun isItemFocused(item: ArtistModel): Boolean {
        return false
    }
}