package com.gamapp.domain.models

import com.google.android.exoplayer2.source.ShuffleOrder

fun emptyPlayList() = PlayList(emptyList())
data class PlayList(
    val tracks: List<TrackModel>,
    val shuffle: Boolean = false,
    val shuffleOrder: IntArray = (tracks.indices).map { it }.shuffled().toIntArray()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayList

        if (tracks != other.tracks) return false
        if (shuffle != other.shuffle) return false
        if (!shuffleOrder.contentEquals(other.shuffleOrder)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tracks.hashCode()
        result = 31 * result + shuffle.hashCode()
        result = 31 * result + shuffleOrder.contentHashCode()
        return result
    }

    val order: List<TrackModel> = if (shuffle) {
        shuffleOrder.map {
            tracks[it]
        }
    } else {
        tracks
    }
}

fun PlayList.toShuffleOrder(): ShuffleOrder {
    return ShuffleOrder.DefaultShuffleOrder(
        shuffleOrder,
        System.currentTimeMillis()
    )
}
