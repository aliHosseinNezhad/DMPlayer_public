package com.gamapp.dmplayer.presenter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TrackBySearchFragment : Fragment() {
    lateinit var searchTitle: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchTitle = requireNotNull(arguments?.getString("data"))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return composeView  {
//                val searchViewModel = viewModel<SearchViewModel>()
//                val tracks by remember {
//                    searchViewModel.searchInteracts.tracks.invoke()
//                }.collectAsState(initial = emptyList(), context = Dispatchers.IO)
//                LaunchedEffect(key1 = searchTitle) {
//                    searchViewModel.searchInteracts.setText.invoke(searchTitle)
//                }
//                val dialog = dialogs()
//                CategoryTracks(
//                    tracks = tracks, popups = searchViewModel
//                        .tracksMenu
//                        .menu(dialog).string(),
//                    state = rememberBaseTrackState {
//                        sortBarVisibility =
//                            TrackSortBarVisibility
//                                .Visible(
//                                    searchViewModel.searchInteracts
//                                        .trackOrder.invoke()
//                                )
//                    },
//                    categoryTitle = searchTitle
//                )

        }
    }
}