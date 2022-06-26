package com.gamapp.dmplayer.presenter.ui.screen.listof.linear

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gamapp.data.utils.DefaultQueueIds
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.createQueue
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.dialogs
import com.gamapp.dmplayer.presenter.ui.screen.elements.QueueItem
import com.gamapp.dmplayer.presenter.ui.screen.menu.string
import com.gamapp.dmplayer.presenter.ui.navigation.toFavorite
import com.gamapp.dmplayer.presenter.ui.navigation.toQueue
import com.gamapp.dmplayer.presenter.ui.theme.content
import com.gamapp.dmplayer.presenter.ui.theme.onContent
import com.gamapp.dmplayer.presenter.viewmodel.QueueViewModel

@Composable
fun QueueLists(
    viewModel: QueueViewModel,
    nav: NavHostController
) {
    val queues by remember {
        viewModel.loadQueues()
    }.observeAsState(initial = listOf())
    val dialog = dialogs()
    val group = remember(queues) {
        queues.groupBy { it.default }
    }
    val defaults = remember(group) {
        group[true] ?: emptyList()
    }
    val nonDefaults = remember(group) {
        group[false] ?: emptyList()
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            QueueTopBar()
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(1.dp),
                color = MaterialTheme.colors.onSurface.copy(0.1f),
                thickness = 1.dp
            )
        }
        items(defaults) { queue ->
            QueueItem(
                queueModel = queue,
                defaultImage = if (queue.id ==
                    DefaultQueueIds.Favorite
                ) R.drawable.ic_heart_filled else R.drawable.ic_queues,
                popupList = viewModel.defaultQueueItemMenu.menu(queue, dialog).string()
            ) {
                if (DefaultQueueIds.Favorite == queue.id)
                    nav.toFavorite()
                else nav.toQueue(queue.id)
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        item {
            Divider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(1.dp),
                color = MaterialTheme.colors.onSurface.copy(0.1f),
                thickness = 1.dp,
                startIndent = 0.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        items(nonDefaults) { queue ->
            QueueItem(
                queueModel = queue,
                popupList = viewModel.nonDefaultQueueItemMenu.menus(queue, dialog).string()
            ) {
                nav.toQueue(queue.id)
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

@Composable
fun QueueTopBar(viewModel: QueueViewModel = hiltViewModel()) {
    val dialog = dialogs()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .fillMaxHeight()
            .wrapContentHeight()
            .clip(
                RoundedCornerShape(100)
            )
            .background(MaterialTheme.colors.content.copy(0.35f))
            .clickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = rememberRipple()) {
                dialog.createQueue {
                    viewModel.queueInteracts.create.invoke(it)
                }
            }) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(id = R.string.queue_create_title),
                fontSize = 15.sp,
                color = MaterialTheme.colors.onContent,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(CenterVertically),
                fontWeight = FontWeight.Normal
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(CenterVertically)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
                    modifier = Modifier
                        .size(17.dp)
                        .align(Center),
                    tint = MaterialTheme.colors.onContent
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
    }
}