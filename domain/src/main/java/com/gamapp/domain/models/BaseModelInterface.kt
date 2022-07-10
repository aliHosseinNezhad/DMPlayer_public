package com.gamapp.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface Id {
    val id: Any
}

interface LongId : Id {
    override val id: Long
}

interface Description : Id {
    val title: String
    val subtitle: String
}

interface Image : Description {
    val imageId: Long
    val defaultImage: Int
}

interface BaseTrack : Image, LongId,Parcelable {
    val fileName: String
    val artist: String
    val album: String
    val dateAdded: Long
    val duration: Int
}

