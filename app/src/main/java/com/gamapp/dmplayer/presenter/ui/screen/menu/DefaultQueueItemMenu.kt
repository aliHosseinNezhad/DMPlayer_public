package com.gamapp.dmplayer.presenter.ui.screen.menu

import androidx.compose.runtime.Composable
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.*
import com.gamapp.domain.models.QueueModel
import com.gamapp.domain.usecase.interacts.QueueInteracts
import javax.inject.Inject

typealias QueueMenuTypeAlias = (Pair<StringResource, () -> Unit>)

@Composable
fun List<QueueMenuTypeAlias>.string(): List<Pair<String, () -> Unit>> {
    return map {
        it.first.string() to it.second
    }
}

class DefaultQueueItemMenu @Inject constructor(
    private val queueInteracts: QueueInteracts
) {
    fun menu(queue: QueueModel, dialog: DialogsState): List<QueueMenuTypeAlias> {
        return listOf(
            StringResource(R.string.clear) to {
                dialog.show(
                    RemoveDialogData(
                        WarningDialogTexts.ClearQueue,
                        onAccept = {
                            queueInteracts.clear.invoke(queue)
                        },
                        onCancel = {}
                    )
                )
            }
        )
    }
}