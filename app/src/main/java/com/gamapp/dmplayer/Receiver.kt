package com.gamapp.dmplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.gamapp.dmplayer.framework.service.receiver


inline fun filter(scope: IntentFilter.() -> Unit): IntentFilter {
    val intentFilter = IntentFilter()
    intentFilter.scope()
    return intentFilter
}

fun Context.broadcast(action: String) {
    sendBroadcast(Intent(action))
}

@Composable
fun RegisterReceiver(receiver: BroadcastReceiver, filter: IntentFilter, key: Any? = null) {
    val context = LocalContext.current
    DisposableEffect(key1 = key) {
        with(context) {
            registerReceiver(receiver, filter)
            onDispose {
                unregisterReceiver(receiver)
            }
        }
    }
}


@Composable
inline fun rememberReceiver(crossinline scope: @DisallowComposableCalls (Context, Intent) -> Unit): BroadcastReceiver {
    val receiver = remember {
        receiver { context, intent ->
            scope(context, intent)

        }
    }
    return receiver
}