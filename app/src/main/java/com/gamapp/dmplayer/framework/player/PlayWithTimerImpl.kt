package com.gamapp.dmplayer.framework.player

import com.gamapp.domain.player_interface.PlayerConnector
import com.gamapp.domain.player_interface.PlayerData
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.*
import javax.inject.Inject

interface PlayWithTimer:Player.Listener {
    enum class TaskState {
        NotInitialized,
        Initialized,
        NotStarted,
        Started,
        Stopped,
        End,
    }

    interface TaskObserver {
        fun onTick(current: Long)
        fun onStateChanged(state: TaskState)
        fun onDurationChange(duration: Long)
    }

    fun start()
    fun cancel()
    fun pause()
    fun setNewTask(duration: Long)
    fun registerTaskObserver(taskObserver: TaskObserver)
    fun unRegisterTaskObserver()
}
class PlayWithTimerImpl @Inject constructor(
    private val player: PlayerConnector,
    private val playerData: PlayerData,
) : PlayWithTimer, Player.Listener {
    var initialized: Boolean = false
    var current: Long = 0
        set(value) {
            field = value
            taskObserver?.onTick(value)
        }
    var state: PlayWithTimer.TaskState = PlayWithTimer.TaskState.NotInitialized
        set(value) {
            field = value
            taskObserver?.onStateChanged(value)
            initialized = value != PlayWithTimer.TaskState.NotInitialized
        }
    var duration: Long = 0
        set(value) {
            field = value
            taskObserver?.onDurationChange(value)
        }
    private var taskObserver: PlayWithTimer.TaskObserver? = null
        set(value) {
            field = value
            if (value != null) {
                value.onDurationChange(duration)
                value.onTick(current)
                value.onStateChanged(state)
            }
        }
    private val scope = CancelableCoroutine(CoroutineScope(Dispatchers.Main + Job()))


    init {
        reset()
    }

    private fun reset() {
        current = 0L
        duration = 0L
        state = PlayWithTimer.TaskState.NotInitialized
    }

    override fun start() {
        with(playerData) {
            if (isPlaying.value)
                scope.start {
                    state = PlayWithTimer.TaskState.Started
                    while (current < this@PlayWithTimerImpl.duration) {
                        current++
                        delay(1000)
                    }
                    onEnd()
                }
        }
    }

    private fun onEnd() {
        scope.stop()
        scope.launch {
            player.pause()
            state = PlayWithTimer.TaskState.End
            reset()
        }
    }

    override fun cancel() {
        pause()
        reset()
    }

    override fun pause() {
        scope.stop()
        state = PlayWithTimer.TaskState.Stopped
    }

    override fun setNewTask(duration: Long) {
        scope.stop()
        this.current = 0
        this.duration = duration
        this.state = PlayWithTimer.TaskState.Initialized
        if (playerData.isPlaying.value) {
            start()
        } else {
            state = PlayWithTimer.TaskState.NotStarted
        }
    }

    override fun registerTaskObserver(taskObserver: PlayWithTimer.TaskObserver) {
        this.taskObserver = taskObserver
    }

    override fun unRegisterTaskObserver() {
        this.taskObserver = null
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (initialized){
            if (isPlaying){
                if (state != PlayWithTimer.TaskState.Started) {
                    start()
                }
            } else {
                if (state == PlayWithTimer.TaskState.Started) {
                    pause()
                }
            }
        }
    }
}