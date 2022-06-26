package com.gamapp.dmplayer.presenter.ui.screen.elements.sort_elements

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks.BaseTrackState
import com.gamapp.dmplayer.presenter.ui.screen.ext.BottomRoundClip
import com.gamapp.dmplayer.presenter.ui.theme.content
import com.gamapp.dmplayer.presenter.ui.theme.onContent
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.sealedclasses.Sort
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun TrackSortLayout(
    state: BaseTrackState,
    selection: Boolean,
    trackSort: MutableStateFlow<Sort<TrackModel>>,
    onPlay: () -> Unit,
    onShuffle: () -> Unit,
) {
    val showTop = state.showTopColor
    val transition = updateTransition(targetState = showTop, label = null)
    val v by transition.animateFloat(label = "label", transitionSpec = { tween(400) }) {
        if (it) 1f else 0f
    }
    Box(modifier = Modifier
        .height(75.dp)
        .fillMaxWidth()
        .zIndex(1f)
        .clip(BottomRoundClip(25.dp))
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {}
        )
        .background(MaterialTheme.colors.surface)
        .graphicsLayer {
            alpha = if (selection) 0.3f else 1f
        }) {
        Box(modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = v
            }
            .background(MaterialTheme.colors.onSurface.copy(0.1f)))
        Row(
            modifier = Modifier
                .align(TopCenter)
                .fillMaxWidth()
                .padding(bottom = 25.dp)
                .height(50.dp)
                .padding(horizontal = 16.dp)
        ) {
            SortItem(
                sort = trackSort,
                onSelected = {
                    val sort = trackSort.value
                    trackSort.tryEmit(
                        sort.copy(
                            type = it,
                            order = sort.order
                        )
                    )
                },
                setOrder = {
                    val sort = trackSort.value
                    trackSort.tryEmit(
                        sort.copy(
                            type = sort.type,
                            order = it
                        )
                    )
                },
                enabled = !selection
            )
            Spacer(modifier = Modifier.weight(1f))

            Icon(painter = rememberVectorPainter(ImageVector.vectorResource(id = R.drawable.round_play_arrow_24)),
                contentDescription = null,
                modifier = Modifier
                    .align(CenterVertically)
                    .width(50.dp)
                    .height(30.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.content.copy(0.6f))
                    .clickable(enabled = !selection) {
                        onPlay()
                    }
                    .wrapContentSize(Center)
                    .size(20.dp),
                tint = MaterialTheme.colors.onContent)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(painter = rememberVectorPainter(ImageVector.vectorResource(id = R.drawable.outline_shuffle_on_24)),
                contentDescription = null,
                modifier = Modifier
                    .align(CenterVertically)
                    .width(50.dp)
                    .height(30.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.content.copy(0.6f))
                    .clickable(enabled = !selection) {
                        onShuffle()
                    }
                    .wrapContentSize(Center)
                    .size(20.dp),
                tint = MaterialTheme.colors.onContent)
            Spacer(modifier = Modifier.width(8.dp))
        }
    }

}