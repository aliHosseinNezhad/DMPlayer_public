package com.gamapp.dmplayer.presenter.ui.screen.category

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.presenter.TopBarType
import com.gamapp.data.db.PlayerDatastore
import com.gamapp.dmplayer.presenter.ui.screen.topbar.AppTopBar
import com.gamapp.dmplayer.presenter.ui.screen.topbar.TitleBar
import com.gamapp.dmplayer.presenter.ui.screen.ext.surfaceTheme
import com.gamapp.dmplayer.presenter.ui.screen.listof.grid.AlbumsGridList
import com.gamapp.dmplayer.presenter.ui.screen.listof.grid.ArtistsGridList
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.trackList.AllTracksVerticalList
import com.gamapp.dmplayer.presenter.ui.screen.listof.linear.QueueLists
import com.gamapp.dmplayer.presenter.utils.navigationBarHeight
import com.gamapp.dmplayer.presenter.utils.statusBarHeight
import com.gamapp.dmplayer.presenter.viewmodel.AppViewModel
import com.gamapp.dmplayer.presenter.viewmodel.CategoryViewModel
import com.gamapp.dmplayer.presenter.viewmodel.QueueViewModel
import com.gamapp.dmplayer.presenter.viewmodel.TracksViewModel
import com.gamapp.layout.categorypager.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PagerConnections(categoryPagerState: CategoryPagerState, categoryTitlePagerState: CategoryTitlePagerState): State<TopBarType> {
    val viewModel = hiltViewModel<AppViewModel>()
    val type = viewModel.topBarType.collectAsState(initial = TopBarType.None)
    LaunchedEffect(key1 = type, key2 = categoryPagerState, key3 = categoryTitlePagerState) {
        val topBar = type.value
        val enabled = topBar !is TopBarType.Selection
        categoryTitlePagerState.setEnable(enabled)
        categoryPagerState.setEnable(enabled)
        delay(100)
        if (topBar is TopBarType.Selection) {
            categoryPagerState.animateTo(
                categoryPagerState.currentIndex,
                requesterId = this,
                animationSpec = tween(400)
            )
        }
    }
    LaunchedEffect(key1 = categoryPagerState, key2 = categoryTitlePagerState) {
        launch {
            snapshotFlow {
                categoryPagerState.currentIndexState.value
            }.collect {
                launch {
                    viewModel.setPagerIndex(it.index)
                }
                if (it.requesterId != categoryTitlePagerState) {
                    categoryTitlePagerState.snapTo(it.index, categoryPagerState)
                }
            }
        }
        launch {
            snapshotFlow {
                categoryTitlePagerState.currentIndexState.value
            }.collect {
                if (it.requesterId != categoryPagerState) {
                    categoryPagerState.animateTo(
                        it.index,
                        categoryTitlePagerState,
                        animationSpec = tween(400)
                    )
                }
            }
        }
        launch {
            snapshotFlow {
                categoryPagerState.progress.value
            }.collect {
                categoryTitlePagerState.moveWithProgress(it)
            }
        }
        launch {
            snapshotFlow { categoryTitlePagerState.progress.value }
                .collect {
                    categoryPagerState.checkWithProgress(it)
                }
        }
    }
    return type
}


@Composable
fun MainCategoryPage(
    nav: NavHostController,
    trackViewModel: TracksViewModel,
    categoryViewModel: CategoryViewModel,
    queueViewModel: QueueViewModel
) {
    val onSearch = rememberSaveable {
        mutableStateOf(false)
    }
    val initialIndex = PlayerDatastore.pagerIndex
    val categoryPagerState = rememberCategoryPagerState(initialIndex)
    val categoryTitlePagerState = rememberCategoryTitlePagerState(initialIndex)
    val type by PagerConnections(categoryPagerState = categoryPagerState, categoryTitlePagerState = categoryTitlePagerState)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(top = statusBarHeight(), bottom = navigationBarHeight() + 30.dp)
    ) {
        AppTopBar(onSearch)
        Crossfade(
            targetState = !onSearch.value, modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            animationSpec = tween(400)
        ) {
            if (it)
                Category(
                    nav = nav,
                    categoryTitlePagerState = categoryTitlePagerState,
                    categoryPagerState = categoryPagerState,
                    type = type,
                    categoryViewModel = categoryViewModel,
                    queueViewModel = queueViewModel,
                    trackViewModel = trackViewModel
                )
            else {
                BackHandler(onSearch.value) {
                    onSearch.value = false
                }
                if (onSearch.value)
                    Search(modifier = Modifier.fillMaxSize(), nav = nav)
            }
        }
//        CrossFadeLayout(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f),
//            one = {
//                Category(
//                    nav = nav,
//                    categoryTitlePagerState = categoryTitlePagerState,
//                    categoryPagerState = categoryPagerState,
//                    type = type,
//                    categoryViewModel = categoryViewModel,
//                    queueViewModel = queueViewModel,
//                    trackViewModel = trackViewModel
//                )
//            },
//            two = {
//                BackHandler(onSearch.value) {
//                    onSearch.value = false
//                }
//                if (onSearch.value)
//                    Search(modifier = Modifier.fillMaxSize(), nav = nav)
//            },
//            animationSpec = tween(400),
//            showOne = !onSearch.value
//        )
    }
}

@Composable
fun Category(
    nav: NavHostController,
    categoryTitlePagerState: CategoryTitlePagerState,
    categoryPagerState: CategoryPagerState,
    type: TopBarType,
    categoryViewModel: CategoryViewModel,
    queueViewModel: QueueViewModel,
    trackViewModel: TracksViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TitleBar(categoryTitlePagerState, type is TopBarType.Selection)
        CategoryPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .surfaceTheme(), state = categoryPagerState
        ) {
            AlbumsGridList(
                nav = nav,
                albumsViewModel = categoryViewModel,
            )
            ArtistsGridList(
                nav = nav,
                categoryViewModel = categoryViewModel,
            )
            QueueLists(nav = nav, viewModel = queueViewModel)
            AllTracksVerticalList(viewModel = trackViewModel, nav = nav)
        }
    }
}