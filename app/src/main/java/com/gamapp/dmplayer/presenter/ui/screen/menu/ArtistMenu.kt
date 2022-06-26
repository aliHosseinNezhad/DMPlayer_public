package com.gamapp.dmplayer.presenter.ui.screen.menu

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.StringResource
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.dialogs
import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.usecase.interacts.Interacts
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

typealias ArtistMenuTypeAlias = (Pair<StringResource, (track: ArtistModel) -> Unit>)
typealias ArtistStringMenuTypeAlias = Pair<String, (track: ArtistModel) -> Unit>
@Composable
fun List<ArtistMenuTypeAlias>.string(): List<ArtistStringMenuTypeAlias> {
    return map {
        it.first.string() to it.second
    }
}
class ArtistMenu @Inject constructor(
    private val act: Interacts,
    @ApplicationContext private val context: Context
) {
    @Composable
    fun menu(nav: NavHostController): List<ArtistStringMenuTypeAlias> {
        val dialog = dialogs()
        val scope = rememberCoroutineScope()
        val menus: List<ArtistMenuTypeAlias> = remember {
            listOf(
                StringResource(R.string.remove) to {

                }
            )
        }
        return menus.string()
    }
}