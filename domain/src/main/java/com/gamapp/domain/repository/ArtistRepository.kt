package com.gamapp.domain.repository

import androidx.activity.result.ActivityResultRegistryOwner
import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.sealedclasses.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface ArtistRepository {
    val artistSortOrder: MutableStateFlow<Sort<ArtistModel>>
    fun artists(): Flow<List<ArtistModel>>
}