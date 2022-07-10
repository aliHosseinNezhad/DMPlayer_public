package com.gamapp.domain.models

import com.gamapp.domain.R
import kotlinx.parcelize.Parcelize
import java.time.Duration

class DescriptionModel(
    override val id: Long,
    override val title: String,
    override val subtitle: String
) : Description, LongId

@Parcelize
class BaseTrackModel(
    override val id: Long,
    override val fileName: String,
    override val title: String,
    override val subtitle: String,
    override val imageId: Long = id,
    override val defaultImage: Int = R.drawable.ic_track,
    override val artist: String,
    override val album: String,
    override val dateAdded: Long,
    override val duration: Int
) : BaseTrack