package com.gamapp.dmplayer.presenter.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamapp.dmplayer.presenter.ui.screen.menu.DefaultQueueItemMenu
import com.gamapp.dmplayer.presenter.ui.screen.menu.NonDefaultQueueItemMenu
import com.gamapp.domain.models.QueueModel
import com.gamapp.domain.repository.QueueRepository
import com.gamapp.domain.usecase.interacts.QueueInteracts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val queueRepository: QueueRepository,
    val queueInteracts: QueueInteracts,
    val nonDefaultQueueItemMenu: NonDefaultQueueItemMenu,
    val defaultQueueItemMenu: DefaultQueueItemMenu
) : ViewModel(), QueueRepository by queueRepository {
    fun loadQueues(): MutableLiveData<List<QueueModel>> {
        val queues = MutableLiveData<List<QueueModel>>()
        viewModelScope.launch {
            queueInteracts.getQueues.invoke().collect {
                queues.postValue(it)
            }
        }
        return queues
    }
    fun remove(it: QueueModel) {
        viewModelScope.launch(context = Dispatchers.IO) {
            queueInteracts.removeQueue.invoke(it)
        }
    }
}