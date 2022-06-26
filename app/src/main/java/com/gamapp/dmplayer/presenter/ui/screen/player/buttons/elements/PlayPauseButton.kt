package com.gamapp.dmplayer.presenter.ui.screen.player.buttons.elements

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.airbnb.lottie.LottieAnimationView
import com.gamapp.custom.CustomIconButton
import com.gamapp.domain.usecase.player.PlayPauseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


@Composable
fun PlayPauseButton(
    modifier: Modifier,
    isPlaying: State<Boolean>,
    playPauseUseCase: PlayPauseUseCase,
    clickable: State<Boolean>
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    CustomIconButton(
        enabled = clickable.value,
        onClick = {
            scope.launch {
                playPauseUseCase.invoke()
            }
        },
        modifier = Modifier.then(modifier)
    ) {
        AndroidView(
            factory = {
                LottieAnimationView(it).apply {
                    isClickable = false
                    isFocusable = false
                    speed = 0f
                    layoutParams = ViewGroup.LayoutParams(-1, -1)
                    setAnimation("music_player_ic_control_play_to_pause.json")
                    snapshotFlow {
                        isPlaying.value
                    }.asLiveData().observe(lifecycleOwner) { playing ->
                        if (playing && speed != 1f) {
                            speed = 1f
                            playAnimation()
                        } else if (!playing && speed != -1f) {
                            speed = -1f
                            playAnimation()
                        }
                    }
                }
            }, modifier = Modifier
                .padding(all = 5.dp)
                .fillMaxSize()
        )
    }
}
//
//fun <T> State<T>.flow(): Flow<T> {
//    return snapshotFlow {
//        value
//    }
//}
//fun <T> State<T>.liveData(context:CoroutineContext = EmptyCoroutineContext): LiveData<T> {
//    return flow().asLiveData(context = context,)
//}