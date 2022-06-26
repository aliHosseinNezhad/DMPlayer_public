package com.gamapp.dmplayer.presenter.ui.screen.dialog.add_musics_to_queues_dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamapp.dmplayer.presenter.ui.theme.onContent
import com.gamapp.dmplayer.presenter.ui.theme.onDialog
import com.gamapp.domain.models.TrackModel

@Composable
fun AddTrackToQueueText(tracks: List<TrackModel>) {
    val dialog = if (tracks.size > 1)
        "${tracks.size} Tracks"
    else "Track"
    Text(
        text = "Add $dialog To :",
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 25.dp),
        color = MaterialTheme.colors.onDialog,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    )
}