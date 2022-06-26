package com.gamapp.dmplayer.presenter.ui.screen.dialog.add_musics_to_queues_dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.EditTextDialogData
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.EditTextDialogTexts
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.dialogs
import com.gamapp.dmplayer.presenter.ui.theme.content
import com.gamapp.dmplayer.presenter.ui.theme.onDialog
import com.gamapp.dmplayer.presenter.viewmodel.QueueViewModel


@Composable
fun CreateQueueButton() {
    val viewModel: QueueViewModel = hiltViewModel()
    val dialog = dialogs()
    Box(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(60.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    dialog.show(
                        EditTextDialogData(
                            texts = EditTextDialogTexts.CreateQueue,
                            accept = {
                                viewModel.queueInteracts.create.invoke(it)
                            },
                            cancel = {}
                        )
                    )
                }
                .background(MaterialTheme.colors.content.copy(0.4f))
                .align(Alignment.CenterStart)
                .wrapContentWidth(align = Alignment.CenterHorizontally)
                .padding(horizontal = 8.dp)
                .height(54.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.round_add_24),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(40.dp)
                    .border(
                        1.dp,
                        color = MaterialTheme.colors.onDialog,
                        shape = CircleShape
                    )
                    .padding(4.dp)
                    .align(Alignment.CenterVertically),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onDialog)
            )
            Text(
                text = stringResource(R.string.create_queue),
                maxLines = 1,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically),
                color = MaterialTheme.colors.onDialog,
                fontSize = 17.sp,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Normal
            )

        }

    }
}