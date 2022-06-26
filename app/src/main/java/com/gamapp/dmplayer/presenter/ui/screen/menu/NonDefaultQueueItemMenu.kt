package com.gamapp.dmplayer.presenter.ui.screen.menu

import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.*
import com.gamapp.domain.models.QueueModel
import com.gamapp.domain.usecase.interacts.QueueInteracts
import javax.inject.Inject

//typealias MenuTypeAlias = (Pair<String,() -> Unit>)


class NonDefaultQueueItemMenu @Inject constructor(private val queueInteracts: QueueInteracts) {
    fun menus(queue: QueueModel, dialog: DialogsState): List<QueueMenuTypeAlias> {
        return listOf(
            StringResource(R.string.edit_queue_title) to {
                dialog.show(
                    EditTextDialogData(
                        EditTextDialogTexts.ChangeQueueTitle,
                        accept = {
                            queueInteracts.update.invoke(queue.id, it)
                        },
                        cancel = {},
                        default = queue.title
                    )
                )
            },
            StringResource(R.string.queue_remove_title) to {
                dialog.show(
                    RemoveDialogData(
                        WarningDialogTexts.RemoveQueue,
                        onAccept = {
                            queueInteracts.removeQueue.invoke(queue)
                        },
                        onCancel = {}
                    )
                )
            },
            StringResource(R.string.queue_clear) to {
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