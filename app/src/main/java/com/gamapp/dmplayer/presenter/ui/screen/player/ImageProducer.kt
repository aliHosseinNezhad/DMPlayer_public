package com.gamapp.dmplayer.presenter.ui.screen.player

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.drawable.toBitmap
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Size
import com.gamapp.domain.models.TrackModel
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.utils.*
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import kotlin.math.min
import kotlin.math.roundToInt

@Preview
@Composable
fun Counter() {
    var count = 0
    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                count++
            },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(text = "$count")
        }
    }
}

@Composable
fun ImageProducer(
    trackModel: TrackModel?,
    defaultImage: Int = R.drawable.ic_track,
    maxSize: Float,
    modifier: Modifier
) {
    val context = LocalContext.current
    val bound = maxSize.toInt()
    val intSize = IntSize(bound, bound)
    var originalImage by remember {
        mutableStateOf(null as Bitmap?)
    }
    var image by remember {
        mutableStateOf(null as Bitmap?)
    }
    var default by remember {
        mutableStateOf(null as Bitmap?)
    }
    LaunchedEffect(key1 = trackModel?.id) {
        withContext(fileDispatcher) {
            val id = trackModel?.id
            originalImage = try {
                id!!.toImageByteArray(context = context)?.toImage()
            } catch (e: Exception) {
                null
            }
        }
    }
    LaunchedEffect(
        key1 = originalImage,
        key2 = intSize
    ) {
        if (originalImage != null)
            withContext(fileDispatcher) {
                image = try {
                    val scale = 0.5f
                    originalImage?.scale(intSize, scale)
                } catch (e: Exception) {
                    null
                }
            }
        else image = null
    }
    LaunchedEffect(key1 = intSize) {
        if (intSize.width > 0 && intSize.height > 0 && image == null)
            withContext(fileDispatcher) {
                val bw = intSize.width
                val bh = intSize.height
                val b = (min(bw, bh) * 0.6f).roundToInt()
                val vectorDrawable =
                    VectorDrawableCompat.create(context.resources, defaultImage, null)?.apply {
                        colorFilter = ColorFilter.tint(Color.White).asAndroidColorFilter()
                    }
                default = vectorDrawable?.toBitmap(b, b)
            }
    }
    val entire = when {
        image != null -> {
            image!!
        }
        default != null -> {
            default!!
        }
        else -> null
    }
    if (entire != null)
        Box(
            modifier = Modifier
                .then(modifier)
        ) {
            Image(
                bitmap = remember(entire) {
                    entire.asImageBitmap()
                },
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
}

@Composable
fun AsyncTrackPagerImage(
    id: Long?,
    size: Int,
    modifier: Modifier,
    @DrawableRes defaultImage: Int = R.drawable.ic_track,
    state: PlayerData
) {
    val sizeState by rememberUpdatedState(newValue = size)
    val fs = remember(sizeState) {
        (sizeState / 12f).toInt()
    }
    var finished by remember {
        mutableStateOf(false)
    }
    val showOrigin by remember {
        derivedStateOf {
            if (state.motionValue != 1f) finished = false
            state.motionValue == 1f
        }
    }
    val lightAlpha by remember {
        derivedStateOf {
            if (state.motionValue < 1f || !finished || !showOrigin) 1f else 0f
        }
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (showOrigin)
            InternalAsyncTrackPagerImage(
                id = id,
                size = size,
                modifier = Modifier.fillMaxSize(),
                finished = {
                    finished = it
                },
                defaultImage = defaultImage
            )
        InternalAsyncTrackPagerImage(
            id = id,
            size = fs,
            modifier = Modifier.fillMaxSize(),
            alpha = lightAlpha,
            defaultImage = defaultImage
        )
    }

}


@Composable
fun InternalAsyncTrackPagerImage(
    id: Long?,
    size: Int,
    modifier: Modifier,
    @DrawableRes defaultImage: Int = R.drawable.ic_track,
    finished: (Boolean) -> Unit = {},
    alpha: Float = 1f
) {
    val context = LocalContext.current
    var buffer by remember {
        mutableStateOf<ByteBuffer?>(null)
    }
    var loadingVisibility by remember {
        mutableStateOf(false)
    }
    var requestVisibility by remember {
        mutableStateOf(false)
    }
    val internalAlpha by remember {
        derivedStateOf {
            if (loadingVisibility && requestVisibility) 1f else 0f
        }
    }
    LaunchedEffect(key1 = id) {
        loadingVisibility = false
        val bytes = id?.toImageByteArray(context)
        buffer = bytes?.toBuffer()
        delay(5)
        loadingVisibility = true
    }
    val ir = remember(buffer, defaultImage) {
        ImageRequest.Builder(context).apply {
            if (buffer == null)
                data(defaultImage)
            else
                data(buffer)
                    .allowRgb565(true)

        }.precision(Precision.INEXACT)
            .build()
    }.let {
        remember(it, size) {
            it.newBuilder().size(Size(size, size))
                .allowHardware(true)
                .build()
        }
    }
    LaunchedEffect(key1 = Unit) {
        snapshotFlow { internalAlpha }.collect {
            if (it == 1f) {
                //because loading of image happen after finish of image requester so we need to delay for that
                delay(30)
            }
            finished(it == 1f)
        }
    }
    AsyncImage(
        model = ir,
        contentDescription = "track_player_icon",
        modifier = modifier.graphicsLayer {
            this.alpha = min(internalAlpha, alpha)
        },
        filterQuality = FilterQuality.Low,
        transform = {
            requestVisibility = when (it) {
                is AsyncImagePainter.State.Empty -> {
                    false
                }
                is AsyncImagePainter.State.Loading -> {
                    false
                }
                is AsyncImagePainter.State.Success -> {
                    true
                }
                is AsyncImagePainter.State.Error -> {
                    false
                }
            }
            it
        }
    )
}


