package com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope

data class StringResource(@StringRes val res: Int, val args: List<Any> = listOf()) {
    @Composable
    fun string(): String {
        val context = LocalContext.current
        return if (args.isNotEmpty())
            context.getString(res, *args.toTypedArray())
        else
            stringResource(id = res)
    }

    fun string(context: Context): String {
        return if (args.isNotEmpty())
            context.getString(res, *args.toTypedArray())
        else
            return context.getString(res)
    }
}

open class DialogEvent private constructor() {
    abstract class Visible : DialogEvent()
    object InVisible : DialogEvent()
}

class DialogsState {
    private var _event: DialogEvent by mutableStateOf(DialogEvent.InVisible)
    var scope: CoroutineScope? = null

    @Composable
    fun event(): State<DialogEvent> = rememberUpdatedState(newValue = _event)

    fun show(event: DialogEvent.Visible) {
        this._event = event
    }

    fun finish() {
        _event = DialogEvent.InVisible
    }

    @Composable
    fun isVisible(): State<Boolean> =
        rememberUpdatedState(newValue = _event != DialogEvent.InVisible)

}

@Composable
fun dialogs(): DialogsState {
    val state = remember {
        DialogsState()
    }
    Dialogs(state)
    return state
}

@Composable
fun Dialogs(state: DialogsState) {
    state.scope = rememberCoroutineScope()
    EditTextDialogDefaults(state = state)
    WarningDialogDefaults(state = state)
    AddToQueueDialogDefault(dialog = state)
}