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
import com.gamapp.domain.usecase.player.RewindUseCase
import com.gamapp.domain.usecase.player.SkipToPreviousUseCase
import com.gamapp.graphics.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SkipToBackButton(
    modifier: Modifier,
    clickable: State<Boolean>,
    skipToPreviousUseCase: SkipToPreviousUseCase,
    rewindUseCase: RewindUseCase
) {
    val rewindButtonInteractionSource = remember {
        MutableInteractionSource()
    }
    val scope = rememberCoroutineScope()
    CustomIconButton(
        enabled = clickable.value,
        onClick = {
            scope.launch {
                skipToPreviousUseCase.invoke()
            }
        },
        modifier = Modifier.then(modifier),
        interactionSource = rewindButtonInteractionSource,
        onLongClick = {}
    ) {
        val pressed = rewindButtonInteractionSource.collectIsPressedAsState()
        LaunchedEffect(Unit) {
            snapshotFlow { pressed.value }.collectLatest {
                var skip = 500L
                if (it) delay(800)
                while (it) {
                    rewindUseCase(skip)
                    delay(100)
                    skip += 500
                }
            }
        }
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