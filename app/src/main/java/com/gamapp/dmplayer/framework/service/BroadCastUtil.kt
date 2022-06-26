package com.gamapp.dmplayer.framework.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

fun receiver(
    onReceive: (context: Context, intent: Intent) -> Unit,
): BroadcastReceiver {

    return object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onReceive(context ?: return, intent ?: return)
        }
    }
}