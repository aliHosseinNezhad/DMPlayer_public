package com.gamapp.dmplayer.presenter.ui.screen.elements.sort_elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.gamapp.dmplayer.presenter.ui.screen.ext.BottomRoundClip
import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.sealedclasses.Sort
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun ArtistSortLayout(
    selection: Boolean = false,
    artistSort: MutableStateFlow<Sort<ArtistModel>>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .then(modifier)
            .graphicsLayer {
                alpha = if (selection) 0.3f else 1f
            }
            .height(75.dp)
            .fillMaxWidth()
            .clip(BottomRoundClip())
            .background(MaterialTheme.colors.surface)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 16.dp)
        ) {
            SortItem(
                sort = artistSort,
                onSelected = {
                    val sort = artistSort.value
                    artistSort.tryEmit(
                        sort.copy(type = it, order = sort.order)
                    )
                },
                enabled = !selection,
                setOrder = {
                    val sort = artistSort.value
                    artistSort.tryEmit(
                        sort.copy(type = sort.type, order = it)
                    )
                }
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}