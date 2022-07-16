package com.gamapp.dmplayer.presenter.activities

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.framework.utils.mediastore_utils.captureTracks
import com.gamapp.domain.models.TrackModel
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShareActivity : ComponentActivity() {
    companion object {
        const val TAG = "ShareActivityTAG"
    }

    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type = intent.type ?: ""
        if ("audio/" in type) {
            val data = intent.data
            if (data != null) {
                val item = MediaItem.fromUri(data)
                Log.i(TAG, "onCreate: ${item.mediaId}")
                startMediaPlayer(data)
                try {
                    val cursor = contentResolver.query(data, null, null, null, null)
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO){
                            var trackModel: TrackModel? = null
                            cursor.captureTracks {
                                trackModel = it
                                finishLoop()
                            }
                        }
                    }
                } catch (e:Exception){
                    e.printStackTrace()
                    Toast.makeText(this, getString(com.gamapp.domain.R.string.error_app_not_able_to_play), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun startMediaPlayer(uri: Uri) {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(this, uri)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
    }
}