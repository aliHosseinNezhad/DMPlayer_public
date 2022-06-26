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
class TrackByQueueFragment : Fragment() {
    private val trackViewModel by viewModels<TracksViewModel>()
    lateinit var queueId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queueId = requireNotNull(arguments?.getString("data"))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return composeView {
//            TracksByQueue(viewModel = trackViewModel, queueId = queueId )
        }
    }
}