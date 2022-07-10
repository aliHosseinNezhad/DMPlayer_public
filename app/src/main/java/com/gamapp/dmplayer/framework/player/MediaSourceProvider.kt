package com.gamapp.dmplayer.framework.player

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.gamapp.domain.mapper.bundle
import com.gamapp.domain.mapper.getTrackModel
import com.gamapp.domain.models.BaseTrack
import com.gamapp.domain.models.TrackModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

fun Long.toMusicUri(): Uri {
    return ContentUris.withAppendedId(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        this
    )
}

fun TrackModel.toMediaMetaData(): MediaMetadataCompat {
    return MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, id.toMusicUri().toString())
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, subtitle)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, subtitle)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,id.toMusicUri().toString())
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, subtitle)
        .putString(MediaMetadataCompat.METADATA_KEY_DURATION, duration.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, artist)
        .build()
}

fun TrackModel.toMediaDescription():MediaDescriptionCompat {
    return MediaDescriptionCompat.Builder()
        .setMediaId(id.toString())
        .setTitle(title)
        .setSubtitle(subtitle)
        .setMediaUri(id.toMusicUri())
        .setExtras(bundle())
        .build()
}

fun MediaDescriptionCompat.toTrackModel(): TrackModel? {
    return this.extras.getTrackModel()
}

fun MediaMetadataCompat.toMediaItem(): MediaBrowserCompat.MediaItem {
    val desc = MediaDescriptionCompat.Builder()
        .setMediaUri(getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri())
        .setTitle(description.title)
        .setSubtitle(description.subtitle)
        .setMediaId(description.mediaId)
        .setIconUri(description.iconUri)
        .build()
    return MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
}

fun List<MediaMetadataCompat>.asMediaSource(
    dataSourceFactory: DefaultDataSource.Factory,
    concatenatingMediaSource: ConcatenatingMediaSource
): ConcatenatingMediaSource {
    forEach { song ->
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(
                MediaItem.Builder()
                    .setUri(song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI))
                    .setMediaId(song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
                    .build()
            )
        concatenatingMediaSource.addMediaSource(mediaSource)
    }
    return concatenatingMediaSource
}