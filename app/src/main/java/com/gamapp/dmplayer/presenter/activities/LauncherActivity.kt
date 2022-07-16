package com.gamapp.dmplayer.presenter.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.ViewCompat
import androidx.core.view.plusAssign
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.gamapp.data.db.ApplicationDatastore
import com.gamapp.data.db.PlayerDatastore
import com.gamapp.dmplayer.framework.ActivityResultRegisterProvider
import com.gamapp.dmplayer.framework.service.*
import com.gamapp.dmplayer.presenter.ui.navigation.SetUpNavigation
import com.gamapp.dmplayer.presenter.ui.screen.PermissionScreen
import com.gamapp.dmplayer.presenter.ui.screen.PermissionScreenCallback
import com.gamapp.dmplayer.presenter.ui.screen.player.TrackPlayerScreen
import com.gamapp.dmplayer.presenter.ui.theme.PlayerTheme
import com.gamapp.dmplayer.presenter.ui.theme.primary
import com.gamapp.dmplayer.presenter.utils.isPermissionsGranted
import com.gamapp.domain.player_interface.PlayerConnection
import com.gamapp.graphics.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val TAG = "LauncherActivityTAG"

@AndroidEntryPoint
class LauncherActivity : ComponentActivity() {


    @Inject
    lateinit var playerConnection: PlayerConnection

    @Inject
    lateinit var io: ApplicationDatastore

    @Inject
    lateinit var registerProvider: ActivityResultRegisterProvider


    private val connection = object:ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {}
        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupWindowsInsets()
        playerConnection.setup(this)
        registerProvider.setup(this)
        val intent = Intent(this,MediaStoreChangeListenerService::class.java)
        bindService(intent,connection, Context.BIND_AUTO_CREATE)
        setContent {
            PlayerTheme {
                PermissionScreen {
                    val nav = rememberNavController()
                    SetUpNavigation(nav)
                    TrackPlayerScreen(nav)
                }
            }
        }
    }


    private fun setupWindowsInsets() {
        window.setFlags(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE,
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        )
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            insets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        Log.i("MusicServiceTAG", "Activity onDestroy: ")
    }
}

