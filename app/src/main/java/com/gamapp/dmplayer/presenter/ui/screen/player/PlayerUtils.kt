package com.gamapp.dmplayer.presenter.ui.screen.player

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import android.view.Window
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine


const val Expanded = 0
const val Minimized = 1
const val MinimalPlayerHeight = 60
val PlayerHeight = 60.dp
const val PlayerHorizontalPadding = 4

@Composable
fun Space(dp: Dp, percent: Float) {
    Spacer(modifier = Modifier.padding(dp / 2f * percent))
}

@Composable
fun RowScope.SpaceWeight(weight: Float) {
    if (weight > 0f)
        Spacer(modifier = Modifier.weight(weight))
}

@Composable
fun ColumnScope.SpaceWeight(weight: Float) {
    if (weight > 0f)
        Spacer(modifier = Modifier.weight(weight))
}

private fun setLightStatusBar(activity: Activity) {
    val window: Window = activity.window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = Color.Black.toArgb()
}

private fun clearLightStatusBar(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = activity.window.decorView.systemUiVisibility // get current flag
        flags =
            flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // use XOR here for remove LIGHT_STATUS_BAR from flags
        activity.window.decorView.systemUiVisibility = flags
        activity.window.statusBarColor = Color.Black.toArgb() // optional
    }
}

@Composable
fun FullScreen(status: State<Boolean>) {
    val context = LocalContext.current
    val activity = context as Activity
    val isDark = isSystemInDarkTheme()
    LaunchedEffect(Unit) {
        val statusFlow = snapshotFlow { status.value }
        val isDarkFlow = snapshotFlow { isDark }
        statusFlow.combine(isDarkFlow, transform = { a, b ->
            a to b
        }).collectLatest {
            val state = it.first
            val dark = it.second
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.window.insetsController?.let {
                    if (dark) {
                        it.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
                        it.setSystemBarsAppearance(0, APPEARANCE_LIGHT_NAVIGATION_BARS)
                        activity.window.navigationBarColor = Color.White.toArgb()
                    } else {
                        if (state) {
//                            it.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
                            it.setSystemBarsAppearance(0, APPEARANCE_LIGHT_NAVIGATION_BARS)
                            activity.window.navigationBarColor = Color.White.toArgb()
                        } else {
                            activity.window.navigationBarColor = Color.Green.toArgb()
                            it.setSystemBarsAppearance(
                                APPEARANCE_LIGHT_STATUS_BARS,
                                APPEARANCE_LIGHT_STATUS_BARS
                            )
                            it.setSystemBarsAppearance(
                                APPEARANCE_LIGHT_NAVIGATION_BARS,
                                APPEARANCE_LIGHT_NAVIGATION_BARS
                            )
                        }
                    }
                }
            }
        }
    }
}





