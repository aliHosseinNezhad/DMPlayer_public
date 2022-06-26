package com.gamapp.dmplayer.presenter.ui.screen.player.buttons.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.presenter.ui.theme.white
import com.gamapp.domain.usecase.player.FastRewindUseCase
import com.gamapp.graphics.R
import kotlinx.coroutines.launch

@Composable
fun FastRewindButton(modifier: Modifier, clickable: State<Boolean>, rewindUseCase: FastRewindUseCase) {
    val rewindButtonInteractionSource = remember {
        MutableInteractionSource()
    }
    val scope = rememberCoroutineScope()
    CustomIconButton(
        enabled = clickable.value,
        onClick = {
            scope.launch {
                rewindUseCase.invoke()
            }
        },
        modifier = Modifier.then(modifier),
        interactionSource = rewindButtonInteractionSource,
        onLongClick = {}
    ) {
        val pressed1 = rewindButtonInteractionSource.collectIsPressedAsState()
//        LaunchedEffect(key1 = pressed1.value) {
//            if (!pressed1.value) return@LaunchedEffect
//            val wasInPlaying = playViewModel.isPlaying.value
//            delay(300)
//            while (pressed1.value) {
//                delay(100)
//                val x = playViewModel.currentPosition.value - 1000
//                if (x > 0)
//                    playViewModel.seekTo(x)
//                else {
//                    playViewModel.seekTo(playViewModel.duration.value)
//                }
//            }
//            if (wasInPlaying)
//                playViewModel.play()
//        }
        Image(
            painter = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.round_fast_rewind_24)),
            contentDescription = null,
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxSize(),
            colorFilter = ColorFilter.tint(white)
        )
    }
}