package com.gamapp.dmplayer.presenter.activities

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.ViewCompat
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.framework.ActivityRegisterResultResultProviderImpl
import com.gamapp.dmplayer.framework.service.MusicService
import com.gamapp.dmplayer.presenter.ui.theme.PlayerTheme
import com.gamapp.dmplayer.presenter.ui.theme.dark
import com.gamapp.dmplayer.presenter.ui.theme.primary
import com.gamapp.dmplayer.presenter.viewmodel.AppViewModel
import com.google.accompanist.insets.Insets
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

fun Insets.string(): String {
    return "(top:$top , right:$right , left:$left , bottom:$bottom)"
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val appViewModel: AppViewModel by viewModels()
    @Inject
    lateinit var activityRegisterResultProviderImpl: ActivityRegisterResultResultProviderImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRegisterResultProviderImpl.onCreate(this.activityResultRegistry)
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        window.setFlags(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE,
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        )
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            insets
        }
        setContentView(R.layout.activity_main_layout)
        val loader = findViewById<FrameLayout>(R.id.loader)
        loader.apply {
            setBackgroundColor(dark.toArgb())
            addView(ImageView(context).apply {
                layoutParams = FrameLayout.LayoutParams(300, 300).apply {
                    gravity = Gravity.CENTER
                }
                setImageResource(R.drawable.ic_track)
                setColorFilter(primary.toArgb())
            })
        }
        val trackPlayerScreen = findViewById<ComposeView>(R.id.track_player_screen)
        trackPlayerScreen.apply {
            setContent {
                PlayerTheme {
//                    TrackPlayerScreen()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityRegisterResultProviderImpl.onDestroy()
    }
}





