package com.gamapp.dmplayer.presenter.ui.screen.player.buttons.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.presenter.ui.theme.white
import com.gamapp.domain.usecase.player.FastForwardUseCase
import com.gamapp.graphics.R
import kotlinx.coroutines.launch


@Composable
fun FastForward(
    modifier: Modifier,
    clickable: State<Boolean>,
    fastForwardUseCase: FastForwardUseCase
) {
    val forwardButtonInteractionSource = remember {
        MutableInteractionSource()
    }
    val scope = rememberCoroutineScope()
    CustomIconButton(
        enabled = clickable.value,
        onClick = {
            scope.launch {
                fastForwardUseCase.invoke()
            }
        },
        modifier = Modifier.then(modifier),
        interactionSource = forwardButtonInteractionSource,
        onLongClick = {}
    ) {
//        val pressed2 = forwardButtonInteractionSource.collectIsPressedAsState()
//        LaunchedEffect(key1 = pressed2.value) {
//            delay(300)
//            while (pressed2.value) {
//                delay(100)
//                val x = playViewModel.currentPosition.value + 1000
//                if (x < playViewModel.duration.value) {
//                    playViewModel.seekTo(x)
//
//                } else {
////                        if (playViewModel.nextMusic()) {
////                            playViewModel.seekTo((0f).roundToInt())
////                        } else break
//                }
//            }
//        }
        Image(
            painter = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.round_fast_forward_24)),
            contentDescription = null,
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxSize(),
            colorFilter = ColorFilter.tint(white)
        )
    }
}