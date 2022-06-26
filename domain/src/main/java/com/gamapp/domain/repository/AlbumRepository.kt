package com.gamapp.domain.repository

import androidx.activity.result.ActivityResultRegistryOwner
import androidx.lifecycle.LiveData
import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.sealedclasses.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface AlbumRepository {
    fun albums(): Flow<List<AlbumModel>>
    val albumSortOrder: MutableStateFlow<Sort<AlbumModel>>
}