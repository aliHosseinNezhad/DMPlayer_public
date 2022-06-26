package com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults


import androidx.annotation.StringRes
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.dialog.WarningDialog
import com.gamapp.domain.models.TrackModel
import kotlinx.coroutines.launch




open class WarningDialogTexts(
    val accept: StringResource,
    val cancel: StringResource,
    val dialog: StringResource
) {
    constructor(
        @StringRes
        accept: Int,
        @StringRes
        cancel: Int,
        @StringRes
        dialog: Int
    ) : this(
        StringResource(accept),
        StringResource(cancel),
        StringResource(dialog)
    )

    object RemoveQueue :
        WarningDialogTexts(
            accept = R.string.remove,
            cancel = R.string.cancel,
            dialog = R.string.remove_queue_dialog
        )

    object ClearQueue :
        WarningDialogTexts(
            accept = R.string.clear,
            cancel = R.string.cancel,
            dialog = R.string.clear_queue_dialog
        )

    object RemoveTrack : WarningDialogTexts(
        accept = R.string.track_remove_accept,
        cancel = R.string.track_remove_cancel,
        dialog = R.string.track_remove_dialog
    )

    class RemoveTracks(count: Int) : WarningDialogTexts(
        accept = StringResource(R.string.track_remove_accept),
        cancel = StringResource(R.string.track_remove_cancel),
        dialog = StringResource(R.string.tracks_remove_dialog, listOf(count))
    )

    object RemoveTrackFromQueue : WarningDialogTexts(
        accept = StringResource(R.string.remove),
        cancel = StringResource(R.string.cancel),
        dialog = StringResource(R.string.track_remove_from_queue_dialog)
    )
}

data class WarningDialogTheme(
    val accept: @Composable () -> ButtonColors = {
        ButtonDefaults.buttonColors()
    },
    val cancel: @Composable () -> ButtonColors = {
        ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onSurface)
    }
)

open class RemoveDialogData(
    texts: WarningDialogTexts,
    onAccept: suspend () -> Unit,
    onCancel: suspend () -> Unit = {},
    theme: WarningDialogTheme = WarningDialogTheme(accept = {
        ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onSurface)
    })
) : WarningDialogData(texts = texts, onAccept = onAccept, onCancel = onCancel, theme = theme)

open class WarningDialogData(
    val texts: WarningDialogTexts,
    val theme: WarningDialogTheme = WarningDialogTheme(),
    val onAccept: suspend () -> Unit,
    val onCancel: suspend () -> Unit = {},
) : DialogEvent.Visible()


@Composable
fun WarningDialogDefaults(
    state: DialogsState,
) {

    val visibility by state.event()
    val show by remember {
        derivedStateOf {
            visibility is WarningDialogData
        }
    }
    if (show) {
        val scope = rememberCoroutineScope()
        val data = (visibility as WarningDialogData).texts
        WarningDialog(
            visible = state.isVisible().value,
            onAccept = {
                scope.launch {
                    (visibility as WarningDialogData).onAccept()
                }
            },
            onCancel = {
                scope.launch {
                    (visibility as WarningDialogData).onCancel()
                }
            },
            onFinish = {
                state.finish()
            },
            accept = data.accept.string(),
            cancel = data.cancel.string(),
            dialog = data.dialog.string(),
            acceptBtnColors = (visibility as WarningDialogData).theme.accept(),
            cancelBtnColors = (visibility as WarningDialogData).theme.cancel()
        )
    }
}