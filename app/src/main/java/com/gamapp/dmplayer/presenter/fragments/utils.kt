package com.gamapp.dmplayer.presenter.fragments

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.gamapp.dmplayer.presenter.ui.theme.PlayerTheme

 fun Fragment.composeView( content: @Composable () -> Unit): View {
    return ComposeView(requireContext()).apply {
        setContent {
            PlayerTheme(content = content)
        }
    }
}
