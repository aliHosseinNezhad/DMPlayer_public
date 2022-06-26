package com.gamapp.dmplayer.presenter.viewmodel.musicplayer

import android.content.Context
import androidx.compose.animation.core.tween
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.gamapp.dmplayer.presenter.models.TrackPlayModel
import com.gamapp.dmplayer.presenter.ui.screen.player.Minimized
import com.gamapp.dmplayer.presenter.ui.theme.primary
import com.gamapp.dmplayer.presenter.ui.theme.secondary
import com.gamapp.domain.player_interface.PlayerData
import com.gamapp.domain.usecase.interacts.FavoriteInteracts
import com.gamapp.heartanimation.HeartAnimationState
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp


interface TrackPlayerModel : PlayerData {
    val heartAnimationState: HeartAnimationState
    val isDynamicGradientEnable: MutableState<Boolean>

    //    val favorite: State<LiveData<Boolean>>
    val musicModel: State<TrackPlayModel>
    val colors: MutableState<List<Color>>
}

class TrackPlayerModelImp
@Inject constructor(
    @ApplicationContext private val context: Context,
    private val favoriteInteracts: FavoriteInteracts,
    private val playerData: PlayerData
) : TrackPlayerModel, PlayerData by playerData {
    override val heartAnimationState: HeartAnimationState = HeartAnimationState()
    override val isDynamicGradientEnable: MutableState<Boolean> = mutableStateOf(true)
    override val musicModel: MutableState<TrackPlayModel> = mutableStateOf(TrackPlayModel())
    override val colors: MutableState<List<Color>> = mutableStateOf(
        mutableListOf(
            primary,
            secondary
        )
    )

}

@InstallIn(ViewModelComponent::class)
@Module
abstract class PlayerModule {
    @Binds
    @ViewModelScoped
    abstract fun bindPlayerMusicData(
        setMusicDataDelegateImp: TrackPlayerModelImp,
    ): TrackPlayerModel
}





