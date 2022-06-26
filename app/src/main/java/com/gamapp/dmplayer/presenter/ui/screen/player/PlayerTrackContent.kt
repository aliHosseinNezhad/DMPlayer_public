package com.gamapp.dmplayer.presenter.ui.screen.player

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gamapp.dmplayer.presenter.ui.screen.player.buttons.MusicListAndFavorite
import com.gamapp.dmplayer.presenter.ui.screen.ext.TrackPlayer
import com.gamapp.dmplayer.presenter.ui.screen.ext.TrackPlayerScope
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel
import com.gamapp.heartanimation.HeartAnimation
import kotlin.math.min

const val TAG2 = "PlayerTrackContent"

interface Ref<T> {
    val data: State<T>
}

@JvmInline
value class RefImpl<T>(override val data: MutableState<T>) : Ref<T> {
    val readData: T get() = data.value
}

@Composable
fun rememberPlayerTrackContentMeasureScope(pagerData: RefImpl<DpSize>, data: PlayerData) = remember {
    MeasurePolicy { measureables, constraints ->
//        Log.i(TAG2, "PlayerTrackContent: update in measure scope happen")
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val details =
            measureables.firstOrNull { it.layoutId == TrackPlayer.TrackDetails }!!
                .measure(childConstraints)
        val addToFavorite =
            measureables.firstOrNull { it.layoutId == TrackPlayer.AddToFavorite }!!
                .measure(childConstraints)
        val heartAnimation =
            measureables.firstOrNull { it.layoutId == TrackPlayer.HeartAnimation }!!
                .measure(childConstraints)
        val leftHeight = constraints.maxHeight - details.height - addToFavorite.height
        val pagerHeight = min(constraints.maxWidth, leftHeight).coerceAtLeast(0)
        val pager =
            measureables.firstOrNull { it.layoutId == TrackPlayer.Pager }!!.measure(
                Constraints.fixed(constraints.maxWidth, pagerHeight)
            )
        var t = 0
        if (pagerData.readData.width != constraints.maxWidth.toDp()
            && pagerData.readData.height != pagerHeight.toDp()
        ) {
            pagerData.data.value = DpSize(
                constraints.maxWidth.toDp(),
                pagerHeight.toDp()
            )
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            pager.place(0, t, 1f)
            t += pager.height
            details.place(0, t)
            val b = constraints.maxHeight
            addToFavorite.place(0, b - addToFavorite.height)
            heartAnimation.place(
                0,
                b - heartAnimation.height - addToFavorite.height / 2,
                zIndex = 1f
            )
        }
    }
}

@Composable
fun TrackPlayerScope.PlayerTrackContent(state: PlayerData) {
    val playerViewModel = viewModel<PlayerViewModel>()
    val pagerData = remember {
        RefImpl(
            mutableStateOf(DpSize(0.dp, 0.dp))
        )
    }
    val measurePolicy = rememberPlayerTrackContentMeasureScope(pagerData = pagerData,data = state)
    Layout(
        content = {
            TracksImagePager(
                state = state,
                modifier = Modifier
                    .layoutId(TrackPlayer.Pager),
                bound = pagerData.data
            )

            MusicDetails(
                modifier = Modifier
                    .layoutId(TrackPlayer.TrackDetails)
                    .padding(vertical = 12.dp, horizontal = 32.dp)
                    .wrapContentHeight(),
                state = state
            )
            MusicListAndFavorite(
                modifier = Modifier
                    .layoutId(TrackPlayer.AddToFavorite)
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                state = state
            )
            HeartAnimation(
                modifier = Modifier.layoutId(TrackPlayer.HeartAnimation),
                state = playerViewModel.heartAnimationState
            )
        },
        modifier = Modifier.fillMaxSize(),
        measurePolicy = measurePolicy
    )
}