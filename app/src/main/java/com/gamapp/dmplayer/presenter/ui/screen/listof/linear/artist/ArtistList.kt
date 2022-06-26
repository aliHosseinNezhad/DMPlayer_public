package com.gamapp.dmplayer.presenter.ui.screen.listof.artist

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.BaseList
import com.gamapp.domain.models.ArtistModel

@Composable
fun ArtistList(artists: List<ArtistModel>, modifier: Modifier, navigator: (ArtistModel) -> Unit,state:ArtistsState = rememberArtistState{}) {
    BaseList(
        items = artists,
        modifier = modifier,
        other = {},
        onItemClicked = {
            navigator(it)
        },
        state = state,
        isFocused = {
            false
        }
    )
}