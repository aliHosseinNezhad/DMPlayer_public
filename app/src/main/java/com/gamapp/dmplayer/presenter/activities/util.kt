package com.gamapp.dmplayer.presenter.activities

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView

fun Activity.compose(content:@Composable ()->Unit): ComposeView {
    return ComposeView(this).apply {
        setContent(content)
    }
}