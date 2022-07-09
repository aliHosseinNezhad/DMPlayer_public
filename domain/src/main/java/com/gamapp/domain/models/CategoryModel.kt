package com.gamapp.domain.models

interface CategoryModel :Image {
    val tracks: List<TrackModel>
}
