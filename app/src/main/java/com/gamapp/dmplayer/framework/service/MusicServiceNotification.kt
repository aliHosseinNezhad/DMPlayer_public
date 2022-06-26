package com.gamapp.dmplayer.framework.service

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.compose.ui.graphics.toArgb
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.gamapp.dmplayer.Constant.NOTIFICATION_CHANNEL_ID
import com.gamapp.dmplayer.Constant.NOTIFICATION_ID
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.theme.primary
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.gamapp.domain.R.drawable

class MusicServiceNotification constructor(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
) {
    private val notificationManager: PlayerNotificationManager
    fun setPlayer(player: Player?) {
        notificationManager.setPlayer(player)
    }

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)
        notificationManager =
            PlayerNotificationManager.Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
                .setChannelDescriptionResourceId(R.string.notification_channel_description)
                .setChannelNameResourceId(R.string.notification_channel_name)
                .setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
                .setStopActionIconResourceId(drawable.round_close_24)
                .setNotificationListener(notificationListener)
                .setSmallIconResourceId(drawable.ic_track)
                .setPauseActionIconResourceId(drawable.round_pause_24)
                .setPlayActionIconResourceId(drawable.round_play_arrow_24)
                .setRewindActionIconResourceId(drawable.round_fast_rewind_24)
                .setFastForwardActionIconResourceId(drawable.round_fast_forward_24)
                .setPreviousActionIconResourceId(drawable.round_skip_previous_24)
                .setNextActionIconResourceId(drawable.round_skip_next_24)
                .build().apply {
                    setColorized(true)
                    setColor(primary.toArgb())
                    setSmallIcon(drawable.ic_track)
                    setMediaSessionToken(sessionToken)
                    setUseFastForwardAction(false)
                    setUseRewindAction(false)
                    setUseNextAction(true)
                    setUseNextActionInCompactView(true)
                    setUsePreviousAction(true)
                    setUsePreviousActionInCompactView(true)
                    setUseStopAction(true)
                }
    }

    private inner class DescriptionAdapter(
        private val mediaController: MediaControllerCompat
    ) : PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence {
            return mediaController.metadata.description.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return mediaController.sessionActivity
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return mediaController.metadata.description.subtitle.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            Glide.with(context).asBitmap()
                .load(mediaController.metadata.description.iconUri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        callback.onBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) = Unit
                })
            return null
        }

    }
}