package com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults

import androidx.compose.runtime.*
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.dialog.EditTextDialog
import kotlinx.coroutines.launch

open class EditTextDialogTexts(
    val title: StringResource,
    val label: StringResource,
    val accept: StringResource,
    val reject: StringResource
) {
    constructor(title: Int, label: Int, accept: Int, reject: Int) : this(
        title = StringResource(title),
        label = StringResource(label),
        accept = StringResource(accept),
        reject = StringResource(reject),
    )

    object ChangeQueueTitle : EditTextDialogTexts(
        R.string.edit_queue_title,
        R.string.edit_queue_label,
        R.string.edit_queue_accept,
        R.string.edit_queue_cancel
    )

    object CreateQueue : EditTextDialogTexts(
        R.string.queue_create_title,
        R.string.queue_create_label,
        R.string.queue_create_accept,
        R.string.queue_create_cancel,
    )
}

fun DialogsState.createQueue(onAccept: suspend (String) -> Unit) {
    show(EditTextDialogData(
        EditTextDialogTexts.CreateQueue,
        accept = onAccept,
        cancel = {}
    ))
}

class EditTextDialogData(
    val texts: EditTextDialogTexts,
    val accept: suspend (String) -> Unit,
    val cancel: suspend () -> Unit,
    val default: String = ""
) : DialogEvent.Visible()

@Composable
fun EditTextDialogDefaults(state: DialogsState) {
    val event by state.event()
    val scope = rememberCoroutineScope()
    val show by remember {
        derivedStateOf {
            event is EditTextDialogData
        }
    }
    if (show) {
        event as EditTextDialogData
        val texts = (event as EditTextDialogData).texts
        EditTextDialog(
            show = state.isVisible().value,
            title = texts.title.string(),
            label = texts.label.string(),
            acceptBtn = texts.accept.string(),
            cancelBtn = texts.reject.string(),
            initialValue = (event as EditTextDialogData).default,
            onAccept = {
                scope.launch {
                    (event as EditTextDialogData).accept(it)
                }
            },
            onCancel = {
                scope.launch {
                    (event as EditTextDialogData).cancel()
                }
            },
            finish = {
                state.finish()
            })
    }
}