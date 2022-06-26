package com.gamapp.dmplayer.presenter.ui.screen.player.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.viewmodel.musicplayer.PlayerViewModel
import kotlinx.coroutines.launch

@Composable
fun <T> rememberStateOf(block: () -> T): State<T> {
    return remember {
        mutableStateOf(block())
    }
}


class ExtendedPainter : Painter() {
    override val intrinsicSize: Size
        get() = TODO("Not yet implemented")

    override fun DrawScope.onDraw() {

    }

}


@Composable
fun RowScope.FavoriteButton() {
    val viewModel: PlayerViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val playerScreenData by viewModel.musicModel
    val favorite by playerScreenData.isFavorite.observeAsState(initial = false)
    val icon by remember {
        derivedStateOf {
            if (favorite) R.drawable.ic_heart_filled else
                R.drawable.ic_heart_border_2
        }
    }
    CustomIconButton(
        onClick = {
            if (!favorite)
                scope.launch {
                    viewModel.isDynamicGradientEnable.value = false
                    viewModel.heartAnimationState.play()
                    viewModel.isDynamicGradientEnable.value = true
                }
            viewModel.currentTrack.value?.let {
                scope.launch {
                    viewModel.playerInteracts.addToFavoriteUseCase.invoke(
                        track = it
                    )
                }
            }
        },
        modifier = Modifier
            .requiredSize(50.dp)
            .align(Alignment.Bottom),
    ) {
        Icon(
            painter = rememberVectorPainter(image = ImageVector.vectorResource(id = icon)),
            contentDescription = null,
            modifier = Modifier
                .size(20.dp),
            tint = Color.White,
        )
    }
}