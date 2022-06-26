package com.gamapp.dmplayer.framework.data_source.remove

import android.content.Context
import android.os.Build
import com.gamapp.data.data_source.media_store.remove.MediaStoreRemoveTracksDataSource
import com.gamapp.dmplayer.framework.utils.removeTracksR
import com.gamapp.dmplayer.framework.utils.removeTracks
import com.gamapp.domain.ActivityRegisterResultProvider
import com.gamapp.domain.models.TrackModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class MediaStoreRemoveTracksDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resultRegistry: ActivityRegisterResultProvider
) : MediaStoreRemoveTracksDataSource {
    override suspend fun removeTrack(
        track: TrackModel,
    ) {
        removeTracks(listOf(track))
    }

    override suspend fun removeTracks(
        tracks: List<TrackModel>,
    ) {
        val registry = resultRegistry.activityRegister
        if (registry != null) {
            when {
                Build.VERSION.SDK_INT > Build.VERSION_CODES.Q -> {
                    context.removeTracksR(tracks.map { it.id }, registry = registry)
                }
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q -> {
                    context.removeTracks(tracks.map { it.id })
                }
            }
        }
    }
}