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
import androidx.lifecycle.asLiveData
import com.airbnb.lottie.LottieAnimationView
import com.gamapp.custom.CustomIconButton
import com.gamapp.domain.usecase.player.ShuffleUseCase
import kotlinx.coroutines.launch

@Composable
fun ShuffleButton(
    modifier: Modifier,
    clickable: State<Boolean>,
    shuffleUseCase: ShuffleUseCase,
    shuffle: State<Boolean>
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    CustomIconButton(
        enabled = clickable.value,
        onClick = {
            scope.launch {
                shuffleUseCase.invoke()
            }
        },
        modifier = Modifier.then(modifier),
    ) {
        AndroidView(factory = {
            LottieAnimationView(it).apply {
                isClickable = false
                isFocusable = false
                speed = 0f
                layoutParams = ViewGroup.LayoutParams(-1, -1)
                setAnimation("music_player_ic_shuffle_off_to_on.json")
            }.apply {
                snapshotFlow {
                    shuffle.value
                }.asLiveData().observe(lifecycleOwner){ shuffle ->
                    if (shuffle && speed != 1f) {
                        speed = 1f
                        playAnimation()
                    } else if (!shuffle && speed != -1f) {
                        speed = -1f
                        playAnimation()
                    }
                }
            }
        }, modifier = Modifier
            .padding(all = 10.dp)
            .fillMaxSize())
    }
}