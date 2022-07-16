package com.gamapp.dmplayer.presenter.ui.screen.player.buttons.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.presenter.ui.theme.white
import com.gamapp.domain.usecase.player.ForwardUseCase
import com.gamapp.domain.usecase.player.SkipToNextUseCase
import com.gamapp.graphics.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun SkipToNextButton(
    modifier: Modifier,
    clickable: State<Boolean>,
    skipToNextUseCase: SkipToNextUseCase,
    forwardUseCase: ForwardUseCase
) {
    val forwardButtonInteractionSource = remember {
        MutableInteractionSource()
    }
    val scope = rememberCoroutineScope()
    CustomIconButton(
        enabled = clickable.value,
        onClick = {
            scope.launch {
                skipToNextUseCase.invoke()
            }
        },
        modifier = Modifier.then(modifier),
        interactionSource = forwardButtonInteractionSource,
        onLongClick = {}
    ) {
        val pressed = forwardButtonInteractionSource.collectIsPressedAsState()
        LaunchedEffect(Unit) {
            snapshotFlow { pressed.value }.collectLatest {
                if (it) delay(800)
                var skip = 500L
                while (it) {
                    forwardUseCase(skip)
                    delay(100)
                    skip += 500
                }
            }
        }
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