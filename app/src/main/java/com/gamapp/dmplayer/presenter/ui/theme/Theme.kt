package com.gamapp.dmplayer.presenter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.Dialogs
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.dialogs

val DarkColorPalette = darkColors(
    primary = primary,
    background = dark2,
    surface = dark,
    onPrimary = highLight,
    onBackground = light,
    onSurface = highLight
)

val LightColorPalette = lightColors(
    primary = primary,
    background = light,
    surface = highLight,
    onPrimary = highLight,
    onBackground = dark,
    onSurface = dark
)

@Composable
fun PlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val typography = if (darkTheme) DarkTypography else LightTypography

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = Shapes,
        content = {
            Dialogs(LocalDialogManager.current)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                content()
            }
        }
    )
}