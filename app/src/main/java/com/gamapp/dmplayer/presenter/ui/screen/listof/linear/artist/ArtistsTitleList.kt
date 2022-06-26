package com.gamapp.dmplayer.presenter.ui.screen.listof.linear.artist

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gamapp.dmplayer.presenter.ui.screen.elements.empty_list.EmptyArtistsImage
import com.gamapp.dmplayer.presenter.ui.screen.listof.artist.ArtistsState
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.BaseVerticalTitledList
import com.gamapp.domain.models.ArtistModel


@Composable
fun ArtistsTitleList(
    title: String,
    items: List<ArtistModel>,
    state: ArtistsState,
    popupList: List<Pair<String, (ArtistModel) -> Unit>>,
    onArtistClick: (ArtistModel) -> Unit,
    popBack: () -> Unit
) {
    BaseVerticalTitledList(
        title = title,
        items = items,
        state = state,
        modifier = Modifier,
        onEmptyListImage = {
            EmptyArtistsImage()
        },
        onItemClicked = onArtistClick,
        other = {

        },
        popupList = popupList,
        popBack = popBack
    )
}