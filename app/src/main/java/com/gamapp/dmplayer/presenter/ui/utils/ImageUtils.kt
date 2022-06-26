package com.gamapp.dmplayer.presenter.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.player.toBuffer
import com.gamapp.dmplayer.presenter.ui.theme.content
import com.gamapp.dmplayer.presenter.utils.toAudioUri
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import kotlin.math.min
import kotlin.math.roundToInt


fun Bitmap.scale(bound: IntSize, percent: Float = 0.6f): Bitmap {
    val iw = this.width
    val ih = this.height
    val bw = bound.width
    val bh = bound.height
    val b = min(bw, bh) * percent
    val nw = b.toInt()
    val nh = (b * ih / iw.toFloat()).toInt()
    val w = min(nw, iw)
    val h = min(nh, ih)
    return this.scale((w), (h))
}

fun ByteArray.toImage(intSize: IntSize? = null): Bitmap? {
    val bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)
    return if (intSize != null) {
        bitmap.scale(intSize)
    } else bitmap
}

suspend fun Long.toImageByteArray(context: Context): ByteArray? {
    return withContext(Dispatchers.IO) {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(
            context, toAudioUri()
        )
        val data = mmr.embeddedPicture
        mmr.release()
        data
    }

}

val fileDispatcher by lazy {
    Executors.newSingleThreadScheduledExecutor().asCoroutineDispatcher()
}


@Composable
fun NewLoadImage(
    id: Long?,
    modifier: Modifier,
    defaultImage: Int,
    tint: Color,
    withBack: Boolean = true,
    alpha: Float = 0.8f
) {
    Box(
        modifier = Modifier
            .then(modifier)
            .clip(RoundedCornerShape(15))
            .background(
                if (withBack)
                    MaterialTheme.colors.content.copy(alpha)
                else Color.Transparent
            )
            .border(
                1.dp,
                color = if (!withBack) Color.Gray else Color.Transparent,
                shape = RoundedCornerShape(15)
            )

    ) {
        val context = LocalContext.current
        var buffer by remember {
            mutableStateOf<ByteBuffer?>(null)
        }
        LaunchedEffect(key1 = id) {
            val bytes = id?.toImageByteArray(context)
            buffer = bytes?.toBuffer()
        }
        val imageRequest = remember(buffer) {
            ImageRequest.Builder(context)
                .apply {
                    if (buffer == null)
                        data(defaultImage)
                    else {
                        allowHardware(true)
                            .crossfade(true)
                            .data(buffer)
                            .allowRgb565(true)
                            .crossfade(300)
                    }
                }
                .build()
        }
        val tintState = rememberUpdatedState(newValue = tint)
        val colorFilter by remember {
            derivedStateOf {
                if (buffer == null) ColorFilter.tint(tintState.value)
                else null
            }
        }
        val scale = remember(buffer == null) {
            if (buffer == null) Modifier.scale(0.6f) else Modifier
        }
        AsyncImage(
            model = imageRequest,
            filterQuality = FilterQuality.Low,
            contentDescription = "image",
            modifier = Modifier
                .fillMaxSize()
                .then(scale),
            colorFilter = colorFilter
        )
    }
}


@Composable
fun LoadImage(
    id: Long?,
    modifier: Modifier,
    alpha: Float,
    defaultImage: Int = R.drawable.ic_track,
    tint: Color = MaterialTheme.colors.onSurface,
) {
    BoxWithConstraints(modifier = modifier) {
        LoadImage(
            bound = IntSize(
                constraints.maxWidth,
                constraints.maxHeight
            ),
            id = id,
            alpha = alpha,
            defaultImage = defaultImage,
            tint = tint
        )
    }
}


@Composable
private fun BoxWithConstraintsScope.LoadImage(
    bound: IntSize,
    tint: Color,
    defaultImage: Int,
    alpha: Float,
    id: Long?,
) {
    val context = LocalContext.current
    var imageBitmap by remember {
        mutableStateOf(null as ImageBitmap?)
    }
    var default by remember {
        mutableStateOf(null as ImageBitmap?)
    }
    LaunchedEffect(
        key1 = bound.width + bound.height + defaultImage,
        key2 = alpha, key3 = tint
    ) {
        withContext(fileDispatcher) {
            val bw = bound.width
            val bh = bound.height
            val b = (min(bw, bh) * 0.5f).roundToInt()
            val vectorDrawable =
                ResourcesCompat.getDrawable(context.resources, defaultImage, null)?.apply {
                    colorFilter = ColorFilter.tint(tint.copy(alpha)).asAndroidColorFilter()
                }
            default =
                vectorDrawable?.toBitmap(b, b)?.asImageBitmap()
        }
    }
    LaunchedEffect(key1 = id, key2 = bound.width, key3 = bound.height) {
        withContext(fileDispatcher) {
            imageBitmap = try {
                id!!.toImageByteArray(context = context)?.toImage(bound)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }
    val bitmap = imageBitmap ?: default
    if (bitmap != null)
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .graphicsLayer {
                    val scale = if (imageBitmap == null) 0.5f else 1f
                    scaleX = scale
                    scaleY = scale
                },
            contentScale = ContentScale.Crop,
        )

}



