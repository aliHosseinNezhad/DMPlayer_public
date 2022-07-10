package com.gamapp.dmplayer.presenter.ui.screen.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.navigation.*
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.StringResource
import com.gamapp.dmplayer.presenter.ui.screen.elements.LinearItem
import com.gamapp.dmplayer.presenter.ui.screen.elements.empty_list.EmptySearchImage
import com.gamapp.dmplayer.presenter.ui.screen.player.PlayerHeight
import com.gamapp.dmplayer.presenter.ui.screen.ext.surfaceTheme
import com.gamapp.dmplayer.presenter.ui.screen.ext.text_field.Label
import com.gamapp.dmplayer.presenter.ui.screen.ext.text_field.TextFieldModel
import com.gamapp.dmplayer.presenter.ui.screen.ext.text_field.Title
import com.gamapp.dmplayer.presenter.utils.navigationBarHeight
import com.gamapp.dmplayer.presenter.viewmodel.AppViewModel
import com.gamapp.dmplayer.presenter.viewmodel.SearchViewModel
import com.gamapp.domain.models.safe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun Search(
    modifier: Modifier,
    nav: NavHostController
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val requestedTitle = rememberSaveable {
        mutableStateOf("")
    }
    val isEmpty by remember {
        derivedStateOf {
            requestedTitle.value.isEmpty()
        }
    }
    Column(modifier = Modifier.then(modifier)) {
        LaunchedEffect(key1 = requestedTitle.value) {
            delay(50)
            viewModel.setTitle(requestedTitle.value)
        }
        com.gamapp.dmplayer.presenter.ui.screen.ext.text_field.TextField(
            model = TextFieldModel(
                title = Title(StringResource(R.string.search), show = false),
                label = Label(StringResource(R.string.search), show = true),
                enable = true,
                editable = true
            ),
            value = requestedTitle.value,
            onValueChange = {
                requestedTitle.value = it
            }
        )
        Box(modifier = Modifier.weight(1f)) {
            if (isEmpty) {
                EmptySearchImage()
            } else {
                SearchList(requestedTitle = requestedTitle, nav = nav)
            }
        }
    }

}


@Composable
fun SearchList(requestedTitle: MutableState<String>, nav: NavHostController) {
    val viewModel: SearchViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val appViewModel = hiltViewModel<AppViewModel>()
    val albums by remember(viewModel) {
        viewModel.albums()
    }.collectAsState(emptyList())
    val artist by remember(viewModel) {
        viewModel.artists()
    }.collectAsState(emptyList())
    val tracks by remember {
        viewModel.tracks()
    }.collectAsState(emptyList())
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        MinimizedList(
            items = tracks,
            title = stringResource(id = R.string.tracks),
            itemTitle = {
                it.title
            },
            itemSubtitle = {
                it.subtitle
            },
            itemImage = {
                it.id
            },
            max = 5,
            onMoreClick = {
                nav.toTracksBySearch(requestedTitle.value)
            },
            onItemClick = {
                coroutineScope.launch {
                    appViewModel.playerInteracts.setPlayListAndPlay(playList = tracks, current = it)
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        MinimizedList(
            items = artist,
            title = stringResource(id = R.string.artists),
            itemTitle = {
                it.title
            },
            itemSubtitle = {
                it.subtitle
            },
            itemImage = { it.imageId },
            max = 5,
            onMoreClick = {
                nav.toArtistsBySearch(requestedTitle.value)
            },
            onItemClick = {
                nav.toArtist(it.safe.id)
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        MinimizedList(
            items = albums,
            title = stringResource(id = R.string.albums),
            itemTitle = {
                it.title
            },
            itemSubtitle = {
                it.subtitle
            },
            itemImage = { it.imageId },
            max = 5,
            onMoreClick = {
                nav.toAlbumsBySearch(requestedTitle.value)
            },
            onItemClick = {
                nav.toAlbum(it.safe.id)
            }
        )
        Spacer(modifier = Modifier.height(navigationBarHeight() + PlayerHeight))
    }
}

@Composable
inline fun <T> MinimizedList(
    items: List<T>,
    title: String,
    itemTitle: @Composable (T) -> String,
    itemSubtitle: @Composable (T) -> String,
    itemImage: @Composable (T) -> Long,
    @androidx.annotation.IntRange(from = 1)
    max: Int = 5,
    noinline onMoreClick: () -> Unit,
    crossinline onItemClick: (T) -> Unit
) {
    require(max >= 1) { "maximum count must be greater than 1 , current value is $max" }
    if (items.isNotEmpty())
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .padding(top = 16.dp, bottom = 16.dp)
                .wrapContentHeight()
                .surfaceTheme(elevation = 2.dp)
        ) {
            Text(
                text = "$title (${items.size})",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 32.dp)
                    .wrapContentHeight(CenterVertically),
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )
            remember(items) {
                items.slice(IntRange(0, min(items.lastIndex, max - 1)))
            }.forEach {
                LinearItem(
                    title = itemTitle(it),
                    subtitle = itemSubtitle(it),
                    imageId = itemImage(it),
                    defaultImage = R.drawable.ic_artist,
                    onItemClick = {
                        onItemClick(it)
                    }
                )
            }
            Text(
                text = stringResource(R.string.view_all),
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(CenterHorizontally)
                    .graphicsLayer {
                        clip = true
                        shape = RoundedCornerShape(100f)
                    }
                    .clickable(onClick = onMoreClick)
                    .width(120.dp)
                    .height(40.dp)
                    .wrapContentWidth(align = CenterHorizontally)
                    .wrapContentHeight(align = CenterVertically),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.primary
            )
        }
}