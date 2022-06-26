package com.gamapp.dmplayer.presenter.ui.screen.elements

import androidx.compose.animation.*
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.gamapp.custom.CustomIconButton
import com.gamapp.domain.models.QueueModel
import com.gamapp.dmplayer.R

@Composable
fun DialogQueueItem(
    queueModel: QueueModel,
    defaultImage: Int = R.drawable.ic_queues,
    tint: Color = MaterialTheme.colors.onSurface,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    showRemoveButton: Boolean = false,
    onLongClick: (() -> Unit)? = null,
) {
    val imageId by queueModel.imageIdLive.observeAsState()
    val count by queueModel.count.observeAsState(0)
    Row(
        modifier = Modifier
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                role = Role.Button,
                onLongClick = onLongClick,
                onClick = onClick
            )
            .height(70.dp)
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        ItemImage(
            id = imageId,
            defaultImage = defaultImage,
            withBack = false,
            tint = tint
        )
        Spacer(modifier = Modifier.width(16.dp))
        DetailText(
            queueModel.title,
            count.toString() + " track${
                if (count > 1) "s" else ""
            }",
        )
        AnimatedVisibility(
            showRemoveButton,
            enter = expandIn(expandFrom = Center, clip = false) + fadeIn(),
            exit = shrinkOut(shrinkTowards = Center, clip = false) + fadeOut(),
            modifier = Modifier
                .size(40.dp)
                .align(CenterVertically)
        ) {
            CustomIconButton(
                onClick = onRemove,
                modifier = Modifier
                    .size(40.dp)
                    .align(CenterVertically)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_clear_24),
                    contentDescription = "delete from queue",
                    modifier = Modifier.fillMaxSize(0.6f),
                    tint = tint
                )
            }
        }


        Spacer(modifier = Modifier.width(16.dp))
    }
}