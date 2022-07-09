package com.gamapp.dmplayer.presenter.viewmodel.musicplayer

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import com.gamapp.color_palette.createPalette
import com.gamapp.color_palette.getTwoOrDefaults
import com.gamapp.dmplayer.framework.player.PlayerConnection
import com.gamapp.dmplayer.framework.player.PlayerConnectionImpl
import com.gamapp.dmplayer.presenter.models.TrackPlayModel
import com.gamapp.dmplayer.presenter.models.emptyPlayModel
import com.gamapp.dmplayer.presenter.ui.screen.menu.PlayerMenu
import com.gamapp.dmplayer.presenter.ui.theme.primary
import com.gamapp.dmplayer.presenter.ui.theme.secondary
import com.gamapp.dmplayer.presenter.ui.utils.toImage
import com.gamapp.dmplayer.presenter.ui.utils.toImageByteArray
import com.gamapp.domain.models.BaseTrackModel
import com.gamapp.domain.player_interface.PlayerData
import com.gamapp.domain.usecase.data.tracks.GetTracksByIdUseCase
import com.gamapp.domain.usecase.interacts.FavoriteInteracts
import com.gamapp.domain.usecase.interacts.PlayerInteracts
import com.google.android.exoplayer2.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val getTracksByIdUseCase: GetTracksByIdUseCase,
    private val trackPlayerModel: TrackPlayerModelImp,
    _connection: PlayerConnectionImpl,
    val playerInteracts: PlayerInteracts,
    val favoriteInteracts: FavoriteInteracts,
    val playerMenu: PlayerMenu,
) : ViewModel(),
    TrackPlayerModel by trackPlayerModel {
    val playerConnection: PlayerConnection = _connection
    init {
        addCloseable(_connection)
    }
    suspend fun init(context: Context, baseTrack: BaseTrackModel?) {
        val track = if (baseTrack != null) {
            getTracksByIdUseCase(baseTrack.id)
        } else null
        if (track != null) {
            trackPlayerModel.musicModel.value = TrackPlayModel(
                id = track.id,
                title = track.title,
                artist = track.artist,
                isFavorite = favoriteInteracts.isFavorite.invoke(track),
                fileName = track.fileName,
                bitmap = context.bitmap(track.id)
            )
        } else trackPlayerModel.musicModel.value = emptyPlayModel()
        val bitmap = musicModel.value.bitmap
        if (bitmap != null) {
            setPalette(bitmap.asAndroidBitmap())
        } else {
            colors.value = initColorPalette().value
        }
    }

    private suspend fun Context.bitmap(id: Long): ImageBitmap? {
        return try {
            id.toImageByteArray(context = this)?.toImage()?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    private fun setPalette(bitmap: Bitmap) {
        colors.value = bitmap.createPalette().getTwoOrDefaults(primary, secondary)
    }

    private fun initColorPalette(): MutableState<List<Color>> {
        val colors = mutableListOf(
            primary,
            secondary
        )
        return mutableStateOf(colors)
    }

    fun setPlayerListAndCurrent(first: BaseTrackModel, tracks: List<BaseTrackModel>) {

    }
}


