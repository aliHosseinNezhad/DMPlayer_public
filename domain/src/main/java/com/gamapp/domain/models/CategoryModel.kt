package com.gamapp.domain.models

interface CategoryModel :ImagedItemModel {
    val tracks: List<TrackModel>
}
