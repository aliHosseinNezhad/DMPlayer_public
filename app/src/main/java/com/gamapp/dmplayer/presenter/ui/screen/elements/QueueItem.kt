package com.gamapp.dmplayer.presenter.ui.screen.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.gamapp.domain.models.QueueModel
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.popup_menu_layout.TextPopupMenu


@Composable
fun QueueItem(
    queueModel: QueueModel,
    defaultImage: Int = R.drawable.ic_queues,
    showVert: Boolean = true,
    withBack :Boolean = true,
    tint: Color = MaterialTheme.colors.secondary,
    popupList:List<Pair<String,()->Unit>> = listOf(),
    onClick: () -> Unit,
) {
    val imageId by queueModel.imageIdLive.observeAsState()
    val count by queueModel.count.observeAsState(0)
    val showMoreVert = remember {
        mutableStateOf(false)
    }
    Row(modifier = Modifier
        .clickable(interactionSource = remember {
            MutableInteractionSource()
        }, indication = rememberRipple(),
            role = Role.Button) {
            onClick()
        }
        .height(70.dp)
        .padding(horizontal = 16.dp)) {
        ItemImage(
            id = imageId,
            defaultImage = defaultImage,
            withBack = withBack,
        )
        Spacer(modifier = Modifier.width(16.dp))
        DetailText(
            queueModel.title,
            count.toString() + " track${
                if (count > 1) "s" else ""
            }",
        )
        Spacer(modifier = Modifier.width(16.dp))
        if (showVert)
            MoreVert(show = showMoreVert, popupContent = {
                TextPopupMenu(show = showMoreVert.value, popupList = popupList, onDismiss = {
                    showMoreVert.value = false
                })
            })
    }
}