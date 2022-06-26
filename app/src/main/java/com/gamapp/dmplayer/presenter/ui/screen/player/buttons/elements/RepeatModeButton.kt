package com.gamapp.dmplayer.presenter.ui.screen.player.buttons.elements

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.asLiveData
import com.airbnb.lottie.LottieAnimationView
import com.gamapp.custom.CustomIconButton
import com.gamapp.domain.player_interface.RepeatMode
import com.gamapp.domain.usecase.player.RepeatModeUseCase
import kotlinx.coroutines.launch

@Composable
fun RepeatModeButton(
    modifier: Modifier,
    repeatMode: State<RepeatMode>,
    clickable: State<Boolean>,
    repeatModeUseCase: RepeatModeUseCase
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    CustomIconButton(
        enabled = clickable.value,
        onClick = {
            scope.launch {
                repeatModeUseCase.invoke()
            }
        },
        modifier = Modifier.then(modifier),
    ) {
        AndroidView(
            factory = {
                LottieAnimationView(context).apply {
                    isClickable = false
                    isFocusable = false
                    speed = 0f
                    layoutParams = ViewGroup.LayoutParams(-1, -1)
                    //a
                    setAnimation("music_player_ic_repeat_none_to_all.json")
                }.apply {
                    snapshotFlow { repeatMode.value }.asLiveData()
                        .observe(lifecycleOwner) { repeatMode ->
                            when (repeatMode) {
                                RepeatMode.ALL -> {
                                    setAnimation("music_player_ic_repeat_none_to_all.json")
                                    speed = 1f
                                    playAnimation()
                                }
                                RepeatMode.ONE -> {
                                    setAnimation("music_player_ic_repeat_all_to_once.json")
                                    speed = 1f
                                    playAnimation()
                                }
                                RepeatMode.OFF -> {
                                    setAnimation("music_player_ic_repeat_once_to_none.json")
                                    speed = 1f
                                    playAnimation()
                                }
                            }
                        }

                }

            },
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxSize()
        )
    }
}