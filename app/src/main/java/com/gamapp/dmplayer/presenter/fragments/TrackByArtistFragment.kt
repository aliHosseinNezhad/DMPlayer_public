package com.gamapp.dmplayer.presenter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gamapp.dmplayer.presenter.viewmodel.TracksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackByArtistFragment : Fragment() {
    private val trackViewModel by viewModels<TracksViewModel>()
    private var artistId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTracks()
    }
    private fun loadTracks(){
        artistId = requireNotNull(arguments?.getLong("data"))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return composeView {

//            TracksByArtist(viewModel = trackViewModel, artistId = artistId)

        }
    }
}