package com.gamapp.domain.models

import androidx.lifecycle.LiveData
import com.gamapp.graphics.R

fun Int.makeS(): String {
    return if (this > 1) "s"
    else ""
}

data class QueueModel(
    override val id: String,
    override var title: String,
    val count: LiveData<Int>,
    val imageIdLive: LiveData<Long?>,
    val default: Boolean = false,
) : Image {
    override val imageId: Long get() = imageIdLive.value ?: 0L
    override val defaultImage: Int get() = R.drawable.ic_queues
    override val subtitle: String
        get() = run {
            val c = (count.value ?: 0)
            "$c track${c.makeS()}"
        }
}