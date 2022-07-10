package com.gamapp.dmplayer.presenter.ui.screen.track_details

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.broadcast
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.StringResource
import com.gamapp.dmplayer.presenter.ui.screen.ext.text_field.Label
import com.gamapp.dmplayer.presenter.ui.screen.ext.text_field.TextFieldModel
import com.gamapp.dmplayer.presenter.ui.screen.ext.text_field.TextField
import com.gamapp.dmplayer.presenter.ui.screen.ext.text_field.Title
import com.gamapp.dmplayer.presenter.ui.screen.elements.TextIcon
import com.gamapp.dmplayer.presenter.ui.screen.topbar.TrackDetailsTitleTopBar
import com.gamapp.dmplayer.presenter.ui.utils.NewLoadImage
import com.gamapp.dmplayer.presenter.utils.navigationBarHeight
import com.gamapp.dmplayer.presenter.utils.statusBarHeight
import com.gamapp.dmplayer.presenter.viewmodel.TrackDetailViewModel
import com.gamapp.domain.ACTIONS
import com.gamapp.domain.models.BaseTrack
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.utils.toTimeFormat


@Composable
fun TrackDetails(track: BaseTrack, nav: NavHostController) {
    var editable by remember {
        mutableStateOf(false)
    }
    BackHandler(editable) {
        editable = false
    }
    val viewModel = hiltViewModel<TrackDetailViewModel>()
    val models = remember(track, editable) {
        listOf(
            TextFieldModel(
                title = Title(name = StringResource(R.string.title_uppercase)),
                label = Label(name = StringResource(R.string.title), show = false),
                enable = editable
            ) to mutableStateOf(track.title),
            TextFieldModel(
                title = Title(name = StringResource(R.string.artists)),
                label = Label(name = StringResource(R.string.artists), show = false),
                enable = editable
            ) to mutableStateOf(track.artist),
            TextFieldModel(
                title = Title(name = StringResource(R.string.albums)),
                label = Label(name = StringResource(R.string.albums), show = false),
                enable = editable,
            ) to mutableStateOf(track.album),
            TextFieldModel(
                title = Title(StringResource(R.string.duration)),
                label = Label(StringResource(R.string.duration), show = false),
                enable = editable,
                editable = false,
            ) to mutableStateOf(track.duration.toTimeFormat())
        )
    }
    val context = LocalContext.current
    DisposableEffect(key1 = Unit) {
        context.broadcast(
            ACTIONS.PLAYER.HIDE
        )
        onDispose {
            context.broadcast(
                ACTIONS.PLAYER.SHOW
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(top = statusBarHeight())
    ) {
        TrackDetailsTitleTopBar(
            onBackPress = { nav.popBackStack() },
            title = stringResource(id = R.string.track_details)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            if (!editable)
                TextIcon(
                    icon = R.drawable.ic_edit,
                    text = stringResource(id = R.string.edit_track),
                    modifier = Modifier
                        .align(CenterVertically)
                        .padding(end = 16.dp)
                ) {
                    editable = !editable
                }
        }
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val bound = (min(maxWidth, maxHeight) - 20.dp).coerceIn(80.dp, 200.dp)
            NewLoadImage(
                id = track.id, modifier = Modifier
                    .padding(vertical = 32.dp)
                    .size(bound)
                    .align(Alignment.Center), alpha = 1f,
                defaultImage = R.drawable.ic_track,
                tint = MaterialTheme.colors.onBackground.copy(0.6f),
                withBack = true
            )
        }
        LazyColumn(modifier = Modifier
            .graphicsLayer {
                clip = true
                shape = RoundedCornerShape(25.dp)
                shadowElevation = 1.dp.toPx()
            }
            .background(MaterialTheme.colors.surface)
            .fillMaxWidth()
            .weight(1f),
            contentPadding = PaddingValues(top = 16.dp)) {
            items(models) { node ->
                TextField(
                    model = node.first, value = node.second.value, onValueChange = {
                        node.second.value = it
                    },
                    modifier = Modifier.padding(horizontal = 25.dp, vertical = 8.dp)
                )
            }
            item(key = "edit", contentType = "edit") {
                if (editable) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.update(track, title = models.first().second.value)
                            },
                            modifier = Modifier
                                .width(100.dp)
                                .height(50.dp)
                                .align(CenterVertically)
                        ) {
                            Text(
                                text = "update",
                                style = MaterialTheme.typography.button,
                                color = MaterialTheme.colors.onPrimary
                            )
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(navigationBarHeight() + 50.dp))
            }
        }

    }

}
