package com.gamapp.slider

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

data class SliderColors(
    val enabledThumbColor: Color,
    val disabledThumbColor: Color = enabledThumbColor.copy(0.5f),
    val enabledSelectedLineColor: Color,
    val disabledSelectedLineColor: Color = enabledSelectedLineColor.copy(0.5f),
    val enabledLineColor: Color,
    val disabledLineColor: Color = enabledLineColor.copy(0.5f)
) {
    companion object {
        @Composable
        fun colors(
            enabledThumbColor: Color = MaterialTheme.colors.primary,
            disabledThumbColor: Color = enabledThumbColor.copy(0.5f),
            enabledSelectedLineColor: Color = MaterialTheme.colors.primary,
            disabledSelectedLineColor: Color = enabledSelectedLineColor.copy(0.5f),
            enabledLineColor: Color = MaterialTheme.colors.primary.copy(0.5f),
            disabledLineColor: Color = enabledLineColor.copy(0.5f)
        ): SliderColors {
            val colors = remember(
                enabledThumbColor,
                disabledThumbColor,
                enabledSelectedLineColor,
                disabledSelectedLineColor,
                enabledLineColor,
                disabledLineColor
            ) {
                SliderColors(
                    enabledThumbColor = enabledThumbColor,
                    disabledThumbColor = disabledThumbColor,
                    enabledSelectedLineColor = enabledSelectedLineColor,
                    disabledSelectedLineColor = disabledSelectedLineColor,
                    enabledLineColor = enabledLineColor,
                    disabledLineColor = disabledLineColor
                )
            }
            return colors
        }
    }
}