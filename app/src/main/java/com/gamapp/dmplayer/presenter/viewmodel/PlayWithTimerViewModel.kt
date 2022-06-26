package com.gamapp.dmplayer.presenter.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.gamapp.dmplayer.framework.player.PlayWithTimer
import com.gamapp.dmplayer.framework.player.PlayWithTimer.TaskState.*
import com.gamapp.domain.player_interface.PlayerData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayWithTimerViewModel @Inject constructor(
    private val playWithTimer: PlayWithTimer,
    private val playerData: PlayerData,
    private val application: Application
) : ViewModel(), PlayWithTimer.TaskObserver {
    var currentTime = mutableStateOf(0L)
    var duration = mutableStateOf(0L)
    var timerState = mutableStateOf(NotStarted)
    val context: Context get() = application.applicationContext
    fun setTask(seconds: Long) {
        playWithTimer.setNewTask(seconds)
    }

    init {
        playerData.addListener(playWithTimer)
        playWithTimer.registerTaskObserver(this)
    }

    override fun onCleared() {
        super.onCleared()
        playWithTimer.unRegisterTaskObserver()
    }

    override fun onTick(current: Long) {
        currentTime.value = current
    }

    override fun onStateChanged(state: PlayWithTimer.TaskState) {
        timerState.value = state
        if (state == Started) {
            val currentTime = duration.value - currentTime.value
            Toast.makeText(
                context,
                "Timer Started! \n $currentTime seconds to finish player",
                Toast.LENGTH_LONG
            ).show()
        } else if (state == End) {
            Toast.makeText(context, "timer Finished!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDurationChange(duration: Long) {
        this.duration.value = duration
    }

    fun stopTask() {
        playWithTimer.cancel()
    }

}