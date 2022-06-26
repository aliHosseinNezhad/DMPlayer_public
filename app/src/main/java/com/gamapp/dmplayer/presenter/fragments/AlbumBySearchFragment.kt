package com.gamapp.dmplayer.presenter.fragments

import android.os.Bundle
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.fragment.findNavController
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.albumList.AlbumSortBarVisibility
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.albumList.BaseAlbums
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.albumList.rememberAlbumState
import com.gamapp.dmplayer.presenter.ui.screen.player.PlayerHeight
import com.gamapp.dmplayer.presenter.utils.navigationBarHeight
import com.gamapp.dmplayer.presenter.viewmodel.SearchViewModel
import com.gamapp.dmplayer.presenter.utils.statusBarHeight
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumBySearchFragment : ComposeFragment() {
    private lateinit var searchTitle: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchTitle = requireNotNull(arguments?.getString("data"))
    }

    @Composable
    override fun Content() {
        val navController = findNavController()
        val viewModel: SearchViewModel = hiltViewModel()
        val albums by remember {
            viewModel.searchInteracts.albums.invoke()
        }.collectAsState(initial = emptyList())
        LaunchedEffect(key1 = searchTitle) {
            viewModel.searchInteracts.setText.invoke(searchTitle)
        }
        BaseAlbums(
            modifier = Modifier.padding(
                top = statusBarHeight(),
                bottom = navigationBarHeight() + PlayerHeight / 2
            ),
            albums = albums,
            navigator = {
                navController.navigate(
                    R.id.action_albumBySearchFragment_to_trackByAlbumFragment,
                    args = Bundle().apply {
                        putLong("data", it.id)
                    })
            },
            state = rememberAlbumState {
                sortBarVisibility =
                    AlbumSortBarVisibility.Visible(viewModel.searchInteracts.albumOrder.invoke())
            })
    }
}