package com.gamapp.dmplayer.framework.data_source.remove

import android.content.Context
import android.os.Build
import androidx.activity.result.ActivityResultRegistry
import com.gamapp.data.data_source.media_store.remove.MediaStoreRemoveTracksDataSource
import com.gamapp.dmplayer.framework.ActivityResultRegisterProvider
import com.gamapp.dmplayer.framework.utils.removeTracks
import com.gamapp.dmplayer.framework.utils.removeTracksR
import com.gamapp.domain.models.BaseTrack
import com.gamapp.domain.models.TrackModel
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MediaStoreRemoveTracksDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val activityRegisterProvider:ActivityResultRegisterProvider
) : MediaStoreRemoveTracksDataSource {
    override suspend fun removeTrack(
        track: BaseTrack,
    ) {
        removeTracks(listOf(track))
    }

    override suspend fun removeTracks(
        tracks: List<BaseTrack>,
    ) {
        val registry = activityRegisterProvider.activityResultRegistry
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