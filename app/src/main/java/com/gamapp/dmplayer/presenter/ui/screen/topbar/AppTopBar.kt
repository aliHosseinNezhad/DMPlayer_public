package com.gamapp.dmplayer.presenter.ui.screen.topbar

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.TopBarType
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.StringResource
import com.gamapp.layout.CrossFadeLayout
import com.gamapp.dmplayer.presenter.viewmodel.AppViewModel
import com.gamapp.layout.categorypager.CategoryTitlePager
import com.gamapp.layout.categorypager.CategoryTitlePagerState


@Composable
fun AppTopBar(
    onSearch: MutableState<Boolean>
) {
    val viewModel = hiltViewModel<AppViewModel>()
    val type by viewModel.topBarType.collectAsState(initial = TopBarType.None)
    val showOne = remember {
        derivedStateOf {
            type !is TopBarType.Selection
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .requiredHeight(80.dp)
                .fillMaxWidth()
        ) {
            CrossFadeLayout(
                modifier = Modifier.fillMaxSize(),
                one = {
                    DefaultMenuBar(onSearch)
                },
                two = {
                    if (type is TopBarType.Selection)
                        SelectionBar(type = type as TopBarType.Selection)
                },
                animationSpec = tween(200),
                showOne = showOne
            )
        }

    }
}

@Composable
fun SelectionBar(type: TopBarType.Selection) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 32.dp)
    ) {
        SelectionCheckBox(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(horizontal = 8.dp)
                .wrapContentWidth(align = Alignment.Start),
            type = type
        )
    }
}

@Composable
fun TitleBar(categoryTitlePagerState: CategoryTitlePagerState?, isSelection: Boolean) {
    val names = remember {
        listOf(
            StringResource(R.string.albums),
            StringResource(R.string.artists),
            StringResource(R.string.playlist),
            StringResource(R.string.tracks),
        )
    }.map { it.string() }
    val background = MaterialTheme.colors.background
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = if (isSelection) 0.2f else 1f
            }
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.horizontalGradient(
                        0f to background,
                        0.1f to Color.Transparent,
                        0.9f to Color.Transparent,
                        1f to background,
                        startX = 0f,
                        endX = size.width,
                        tileMode = TileMode.Decal
                    )
                )
            }
            .height(50.dp)
    ) {
        if (categoryTitlePagerState != null)
            CategoryTitlePager(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(BottomCenter),
                content = {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .fillMaxHeight()
                            .wrapContentWidth(CenterHorizontally)
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides contentColorFor(
                                backgroundColor = MaterialTheme.colors.background
                            )
                        ) {
                            Text(
                                text = it,
                                modifier = Modifier.align(Center),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.body1,
                                fontSize = 21.sp,
                                fontWeight = FontWeight(480),
                                color = LocalContentColor.current
                            )
                        }
                    }
                },
                items = names,
                state = categoryTitlePagerState
            )
    }
}

@Composable
fun DefaultMenuBar(onSearch: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .requiredHeight(60.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        CustomIconButton(
            onClick = {
                onSearch.value = !onSearch.value
            }, modifier = Modifier
                .size(50.dp)
                .align(CenterVertically)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_search_24),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colors.onBackground.copy(0.8f)
            )
        }
        CustomIconButton(
            onClick = {}, modifier = Modifier
                .size(50.dp)
                .align(CenterVertically)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_more_vert),
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = MaterialTheme.colors.onBackground.copy(0.8f)
            )
        }
    }
}