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
class TrackByAlbumFragment : Fragment() {
    private val trackViewModel by viewModels<TracksViewModel>()
    private var albumId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTracks()
    }

    private fun loadTracks() {
        albumId = requireNotNull(arguments?.getLong("data"))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return composeView {
//                TracksByAlbum(viewModel = trackViewModel, albumId = albumId)
        }
    }
}