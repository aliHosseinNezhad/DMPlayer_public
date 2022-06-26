package com.gamapp.data.mapper

import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource

fun ConcatenatingMediaSource.toList(): List<MediaSource> {
    return List(size) { index ->
        getMediaSource(index)
    }
}