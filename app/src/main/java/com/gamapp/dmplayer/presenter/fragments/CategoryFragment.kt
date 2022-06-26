package com.gamapp.dmplayer.presenter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gamapp.dmplayer.presenter.viewmodel.CategoryViewModel
import com.gamapp.dmplayer.presenter.viewmodel.QueueViewModel
import com.gamapp.dmplayer.presenter.viewmodel.TracksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : Fragment() {
    companion object {
        const val TAG = "CategoryFragment"
    }

    private val categoryViewModel by viewModels<CategoryViewModel>()
    private val trackViewModel by viewModels<TracksViewModel>()
    private val queueViewModel by viewModels<QueueViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val navController = findNavController()
        return composeView {
//                MusicCategory(
//                    navController = navController,
//                    trackViewModel = trackViewModel,
//                    categoryViewModel = categoryViewModel,
//                    queueViewModel = queueViewModel
//                )
        }
    }
}