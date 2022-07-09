package com.gamapp.dmplayer.presenter.activities

import android.content.Intent
import android.os.Bundle
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
import com.gamapp.dmplayer.framework.service.MusicService
import com.gamapp.dmplayer.presenter.ui.navigation.SetUpNavigation
import com.gamapp.dmplayer.presenter.ui.screen.PermissionScreen
import com.gamapp.dmplayer.presenter.ui.screen.PermissionScreenCallback
import com.gamapp.dmplayer.presenter.ui.screen.player.TrackPlayerScreen
import com.gamapp.dmplayer.presenter.ui.theme.PlayerTheme
import com.gamapp.dmplayer.presenter.ui.theme.primary
import com.gamapp.dmplayer.presenter.utils.isPermissionsGranted
import com.gamapp.domain.player_interface.PlayerConnection
import com.gamapp.domain.player_interface.setupPlayer
import com.gamapp.graphics.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LauncherActivity : ComponentActivity() {

    @Inject
    lateinit var playerConnection: PlayerConnection

    @Inject
    lateinit var io: ApplicationDatastore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupPlayer(playerConnection)
        startMusicService()
        setupWindowsInsets()

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

    private fun startMusicService(){
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        setupWindowsInsets()
    }

    private fun setContent() {
        if (isPermissionsGranted()) {
            setContentView(FrameLayout(this).apply {
                layoutParams = ViewGroup.LayoutParams(-1, -1)
                this += ImageView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(300, 300).apply {
                        gravity = Gravity.CENTER
                    }
                    setImageResource(R.drawable.ic_track)
                    setColorFilter(primary.toArgb())
                }
            })
            startMainActivity()
        } else
            setContent {
                PlayerTheme {
                    PermissionScreenCallback {
                        startMainActivity()
                    }
                }
            }
    }

    private fun startMainActivity() {
        lifecycleScope.launchWhenResumed {
            PlayerDatastore.setData(applicationDatastore = io)
            val intent = Intent(this@LauncherActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}

