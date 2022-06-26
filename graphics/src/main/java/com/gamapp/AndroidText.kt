package com.gamapp

import android.text.TextUtils
import android.view.View.*
import android.widget.TextView
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.asLiveData

@Composable
fun AndroidText(
    text: State<String>,
    textAlign: TextAlign,
    modifier: Modifier,
    color: Color,
    maxLines: Int = Int.MAX_VALUE,
    fontSize: TextUnit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentTextAlign by rememberUpdatedState(newValue = textAlign)
    val textAlignment by remember {
        derivedStateOf {
            when (currentTextAlign) {
                TextAlign.Start -> {
                    TEXT_ALIGNMENT_TEXT_START
                }
                TextAlign.End -> {
                    TEXT_ALIGNMENT_TEXT_END
                }
                TextAlign.Center -> {
                    TEXT_ALIGNMENT_CENTER
                }
                else -> {
                    TEXT_ALIGNMENT_TEXT_START
                }
            }
        }
    }
    AndroidView(factory = {
        TextView(it).apply {
            snapshotFlow {
                text.value
            }.asLiveData().observe(lifecycleOwner){ text ->
                this.text = text
            }
        }
    }, update = {
        it.setTextColor(color.toArgb())
        it.maxLines = maxLines
        it.textSize = fontSize.value
        it.textAlignment = textAlignment
        it.ellipsize = TextUtils.TruncateAt.END
    }, modifier = modifier)

}