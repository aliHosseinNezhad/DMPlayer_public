package com.gamapp.dmplayer.presenter.ui.screen.menu

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.StringResource
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.dialogs
import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.usecase.interacts.Interacts
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

typealias AlbumMenuTypeAlias = (Pair<StringResource, (track: AlbumModel) -> Unit>)
typealias AlbumStringMenuTypeAlias = Pair<String, (track: AlbumModel) -> Unit>
@Composable
fun List<AlbumMenuTypeAlias>.string(): List<AlbumStringMenuTypeAlias> {
    return map {
        it.first.string() to it.second
    }
}

class AlbumMenu @Inject constructor(
    private val act: Interacts,
    @ApplicationContext private val context: Context
) {
    @Composable
    fun menu(nav: NavHostController): List<AlbumStringMenuTypeAlias> {
        val dialog = dialogs()
        val scope = rememberCoroutineScope()
        val menus: List<AlbumMenuTypeAlias> = remember {
            listOf(
                StringResource(R.string.remove) to {

                }
            )
        }
        return menus.string()
    }
}