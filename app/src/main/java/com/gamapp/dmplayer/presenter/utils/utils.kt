package com.gamapp.dmplayer.presenter.utils

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp


@Composable
fun statusBarHeight(): Dp {
    val context = LocalContext.current
    val height = remember {
        context.statusBarHeight()
    }
    return height
}

@Composable
fun navigationBarHeight(): Dp {
    val context = LocalContext.current
    val height = remember {
        context.navigateBarHeight()
    }
    return height
}
fun Long.toAudioUri(): Uri {
    return ContentUris.withAppendedId(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        this
    )
}

