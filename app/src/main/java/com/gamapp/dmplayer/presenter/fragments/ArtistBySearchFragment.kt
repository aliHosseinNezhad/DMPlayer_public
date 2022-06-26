package com.gamapp.dmplayer.presenter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistBySearchFragment : Fragment() {
    private lateinit var searchTitle: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchTitle = requireNotNull(arguments?.getString("data"))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return composeView {
//            val navController = findNavController()
//            val viewModel = hiltViewModel<SearchViewModel>()
//            val artists by remember(viewModel) {
//                viewModel.searchInteracts.artist.invoke()
//            }.collectAsState(initial = emptyList())
//            LaunchedEffect(key1 = searchTitle) {
//                viewModel.searchInteracts.setText.invoke(searchTitle)
//            }
//            ArtistList(
//                artists = artists, modifier = Modifier
//                    .padding(
//                        top = statusBarHeight(),
//                        bottom = navigationBarHeight() + PlayerHeight / 2
//                    )
//                    .fillMaxSize(),
//                navigator = {
//                    navController.navigate(
//                        R.id.action_artistBySearchFragment_to_trackByArtistFragment,
//                        args = Bundle().apply {
//                            putLong("data", it.id)
//                        })
//                },
//                state = rememberArtistState {
//                    sortBarVisibility =
//                        ArtistSortBarVisibility.Visible(viewModel.searchInteracts.artistOrder.invoke())
//                }
//            )

        }
    }
}