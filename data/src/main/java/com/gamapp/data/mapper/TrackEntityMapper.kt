package com.gamapp.data.mapper

import com.gamapp.data.entity.TrackEntity
import com.gamapp.domain.models.TrackModel


fun TrackModel.toTrackEntity(): TrackEntity {
    return TrackEntity(
        fileId = id,
        fileName = fileName,
        title = title,
        artist = artist,
        duration = duration,
        size = size,
        artistId = artistId,
        albumId = albumId,
        album = album,
        dateAdded = dateAdded
    )
}


fun TrackEntity.toTrackModel(): TrackModel {
    return TrackModel(
        fileName = fileName,
        id = fileId,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        size = size,
        albumId = albumId,
        artistId = artistId,
        dateAdded = dateAdded
    )
}
