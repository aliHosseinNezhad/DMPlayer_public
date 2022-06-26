package com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.basetracks

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.elements.sort_elements.TrackSortLayout
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.BaseListState
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.SortBarVisibility
import com.gamapp.dmplayer.presenter.ui.screen.player.PlayerHeight
import com.gamapp.dmplayer.presenter.ui.theme.primary
import com.gamapp.dmplayer.presenter.viewmodel.AppViewModel
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.sealedclasses.Sort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
inline fun rememberBaseTrackState(set: BaseTrackState.() -> Unit): BaseTrackState {
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current
    val viewModel = hiltViewModel<AppViewModel>()
    val state = remember(context, viewModel) {
        BaseTrackState(viewModel).apply(set)
    }.apply {
        this.lazyListState = lazyListState
    }
    return state
}

open class TrackSortBarVisibility private constructor() : SortBarVisibility {
    class Visible(val sort: MutableStateFlow<Sort<TrackModel>>) : TrackSortBarVisibility()
    object Invisible : TrackSortBarVisibility()
}

class BaseTrackState(private val viewModel: AppViewModel) :
    BaseListState<TrackModel>() {
    var showTopColor by mutableStateOf(false)
    var showGoToFirstFloatingButton by mutableStateOf(false)

    override var sortBarVisibility: TrackSortBarVisibility by mutableStateOf(TrackSortBarVisibility.Invisible)
    val nestScroll: NestedScrollConnection = object : NestedScrollConnection {
        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            Snapshot.withMutableSnapshot {
                if (consumed.y < 0f) showTopColor = true
                if (available.y > 0f) showTopColor = false
                showGoToFirstFloatingButton = consumed.y > 0f
                if (available.y > 0f) showGoToFirstFloatingButton = false
            }
            return super.onPostScroll(consumed, available, source)
        }
    }

    @Composable
    fun BoxScope.FloatingButton() {
        val transition = updateTransition(targetState = showGoToFirstFloatingButton, label = null)
        val v by transition.animateFloat(label = "v") {
            if (it) 1f else 0f
        }
        val scope = rememberCoroutineScope()
        CustomIconButton(
            onClick = {
                scope.launch {
                    try {
                        lazyListState.animateScrollToItem(0)
                    } catch (e: Exception) {

                    } finally {
                        showGoToFirstFloatingButton = false
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = PlayerHeight)
                .size(40.dp)
                .graphicsLayer {
                    scaleX = v
                    scaleY = v
                    alpha = v
                    shadowElevation = 8.dp.toPx()
                    clip = true
                    shape = CircleShape
                    rotationZ = 180f
                }
                .background(primary),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_down),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp),
                tint = Color.White
            )
        }
    }

    @Composable
    override fun SortBarContent(items: List<TrackModel>) {
        val scope = rememberCoroutineScope()
        if (sortBarVisibility is TrackSortBarVisibility.Visible) {
            val sortBar = sortBarVisibility as TrackSortBarVisibility.Visible
            TrackSortLayout(
                trackSort = sortBar.sort,
                state = this,
                selection = selectionManager.onSelectionMode,
                onPlay = {
                    if (items.isNotEmpty()) {
                        scope.launch {
                            viewModel.setAndPlay(items, items.first())
                        }
                    }
                },
                onShuffle = {
                    if (items.isNotEmpty()) {
                        scope.launch {
                            val index =
                                Random(System.currentTimeMillis()).nextInt(0, items.size)
                            viewModel.setAndPlay(items, items[index])
                        }
                    }
                })
        }
    }
}
