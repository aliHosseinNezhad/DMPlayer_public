package com.gamapp.dmplayer.presenter.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Color.Companion.MiddleGray: Color
    get() =
        Color(90, 90, 90, 255)

val alpha = 0

// Set of Material typography styles to start with



val LightTypography = typography(dark)


val DarkTypography  = typography(light)


fun typography(color: Color) = run {
    val typography = Typography()
    typography.copy(
        h1 = typography.h1.copy(color = color),
        h2 = typography.h2.copy(color = color),
        h3 = typography.h3.copy(color = color),
        h4 = typography.h4.copy(color = color),
        h5 = typography.h5.copy(color = color),
        h6 = typography.h6.copy(color = color),
        subtitle1 = typography.subtitle1.copy(color = color),
        subtitle2 = typography.subtitle2.copy(color = color),
        body1 = typography.body1.copy(color = color),
        body2 = typography.body2.copy(color = color),
        button = typography.button.copy(color = color),
        caption = typography.caption.copy(color = color),
        overline = typography.overline.copy(color = color)
    )
//    typography
}

@Composable
fun TextStyle.onSelection(selected: Boolean, alpha: Float = 1f): TextStyle {
    return if (selected)
        this.copy(color = MaterialTheme.colors.onSelectionTextColor.copy(alpha))
    else this
}