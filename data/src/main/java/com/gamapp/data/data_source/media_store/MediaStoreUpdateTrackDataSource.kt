package com.gamapp.data.data_source.media_store

import android.content.ContentValues

interface MediaStoreUpdateTrackDataSource {
    suspend fun update(ids: List<Long>, contentValues: ContentValues)
}