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
class TracksByFavorite : Fragment() {
    val tracksViewModel by viewModels<TracksViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return composeView {

//                TracksByFavorite(tracksViewModel)

        }
    }
}