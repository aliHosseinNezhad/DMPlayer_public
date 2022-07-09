package com.gamapp.domain.models

import com.gamapp.domain.R

class DescriptionModel(
    override val id: Long,
    override val title: String,
    override val subtitle: String
) : Description, LongId

class BaseTrackModel(
    override val id: Long,
    override val title: String,
    override val subtitle: String,
    override val imageId: Long = id,
    override val defaultImage: Int = R.drawable.ic_track
) : BaseTrack