package com.gamapp.dmplayer.presenter.ui.screen.elements

import android.widget.ImageView
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.asLiveData
import coil.Coil
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.size.Scale
import coil.util.CoilUtils
import com.gamapp.data.repository.collect
import com.gamapp.dmplayer.presenter.ui.screen.player.buttons.rememberStateOf
import com.gamapp.dmplayer.presenter.ui.screen.player.toBuffer
import com.gamapp.dmplayer.presenter.ui.theme.content
import com.gamapp.dmplayer.presenter.ui.theme.onContent
import com.gamapp.dmplayer.presenter.ui.theme.onSelection
import com.gamapp.dmplayer.presenter.ui.theme.primary
import com.gamapp.dmplayer.presenter.ui.utils.NewLoadImage
import com.gamapp.dmplayer.presenter.ui.utils.toImageByteArray
import com.gamapp.domain.models.ImagedItemModel
import com.gamapp.domain.R
import com.gamapp.layout.rememberState
import com.gamapp.slider.lazyDerivedState
import com.gamapp.timeline.AndroidText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import java.nio.ByteBuffer

@Composable
fun <T : ImagedItemModel> rememberBaseItem(input: T): BaseItem {
    val item = remember {
        BaseItem()
    }.apply {
        set(input)
    }
    return item
}

class BaseItem {
    private val itemImaged = mutableStateOf(null as ImagedItemModel?)

    val title = itemImaged.lazyDerivedState {
        it.title
    }
    val subtitle = itemImaged.lazyDerivedState {
        it.subtitle
    }
    val defaultImage = itemImaged.lazyDerivedState {
        it.defaultImage
    }
    val id = itemImaged.lazyDerivedState {
        it.id
    }
    val imageId = itemImaged.lazyDerivedState {
        it.imageId
    }


    fun <T : ImagedItemModel> set(item: T) {
        itemImaged.value = item
    }
}

@Composable
fun <T : ImagedItemModel> LinearItem(
    modifier: Modifier,
    item: T,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    isFocused: State<Boolean>,
    isSelected: State<Boolean>,
    showPopupMenu: MutableState<Boolean> = rememberState { false },
    popupContent: @Composable (MutableState<Boolean>) -> Unit = {},
    onSelection: Boolean,
) {
    val selected by isSelected
    val focused by isFocused
    val clickGestureModifier = Modifier.combinedClickable(
        interactionSource = remember {
            MutableInteractionSource()
        },
        indication = rememberRipple(),
        role = Role.Button,
        onClick = onClick,
        onLongClick = onLongClick
    )
    Box(
        modifier = Modifier
            .then(clickGestureModifier)
            .then(modifier)
            .fillMaxWidth()
            .height(80.dp)
    ) {
        if (onSelection) {
            val background = if (selected)
                primary.copy(0.3f) else Color.Transparent
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(background)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            ItemImage(item.imageId, defaultImage = item.defaultImage)
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    DetailText(
                        title = item.title,
                        subtitle = item.subtitle,
                        isSelected = focused
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    if (!onSelection)
                        MoreVert(show = showPopupMenu) {
                            popupContent(showPopupMenu)
                        }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colors.onSurface.copy(0.1f)
                )
            }

        }
    }
}

@Composable
fun RowScope.ItemImage(
    id: Long?,
    defaultImage: Int,
    modifier: Modifier = Modifier
        .size(50.dp)
        .align(Alignment.CenterVertically),
    withBack: Boolean = true,
    tint: Color = MaterialTheme.colors.onContent,
) {
    val alpha = if (isSystemInDarkTheme()) 0.8f else 0.8f
    NewLoadImage(
        id = id,
        modifier = modifier,
        defaultImage = defaultImage,
        tint = tint,
        alpha = alpha
    )
}

@Composable
fun Image(item: BaseItem, modifier: Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var imageResult by remember {
        mutableStateOf(null as ImageResult?)
    }
    LaunchedEffect(key1 = item) {
        snapshotFlow { item.imageId.value }.map {
            val bytes = it.toImageByteArray(context)
            bytes?.toBuffer()
        }.collectLatest { buffer ->
            val request = ImageRequest.Builder(context).apply {
                if (buffer != null) {
                    crossfade(true)
                    crossfade(300)
                    allowRgb565(true)
                    data(buffer)
                } else {
                    data(item.defaultImage.value)
                }
            }.build()
            val imageLoader = context.imageLoader
            imageResult = imageLoader.execute(request)
        }
    }
    AndroidView(
        factory = {
            ImageView(it).apply {
                snapshotFlow { imageResult }
                    .asLiveData()
                    .observe(lifecycleOwner) {
                        it?.let {
                            setImageDrawable(it.drawable)
                        }
                    }
            }
        }, modifier = modifier
            .clip(RoundedCornerShape(15))
            .background(
                MaterialTheme.colors.content.copy(0.6f)
            )
            .border(
                1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(15)
            )
    )
}

@Composable
fun RowScope.DetailText(title: String, subtitle: String, isSelected: Boolean = false): Unit =
    CompositionLocalProvider(LocalContentColor provides contentColorFor(backgroundColor = MaterialTheme.colors.surface)) {
        val color =
            if (isSelected) MaterialTheme.colors.primary else LocalContentColor.current
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start),
            ) {

                Text(
                    text = title,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 30.dp),
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body1.onSelection(isSelected),
                    color = color
                )

            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start),
            ) {
                Text(
                    text = subtitle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 30.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.caption.onSelection(
                        isSelected,
                        alpha = 0.6f
                    ),
                    color = color.copy(ContentAlpha.medium)
                )

            }
            Spacer(modifier = Modifier.padding(4.dp))
        }
    }


@Composable
fun RowScope.MoreVert(
    show: MutableState<Boolean>,
    popupContent: @Composable () -> Unit,
) {
    Box(modifier = Modifier
        .size(60.dp)
        .clip(CircleShape)
        .align(Alignment.CenterVertically)
        .clickable {
            show.value = true
        }) {
        Image(
            painter = painterResource(id = R.drawable.ic_more_vert_thin),
            contentDescription = null,
            modifier = Modifier
                .size(15.dp)
                .align(Alignment.Center)
                .alpha(0.7f),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface.copy(0.5f)),
            contentScale = ContentScale.Inside
        )
        popupContent()
    }
}
