package com.gamapp.custom

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CancelableCoroutine(coroutineScope: CoroutineScope) :
    CoroutineScope by coroutineScope {
    var inProgress: Boolean = false
        private set
    private var job: Job? = null
    fun start(work: suspend () -> Unit) {
        job?.cancel()
        inProgress = true
        job = launch {
            work()
            inProgress = false
        }
    }

    fun stop() {
        job?.cancel()
        inProgress = false
    }
}