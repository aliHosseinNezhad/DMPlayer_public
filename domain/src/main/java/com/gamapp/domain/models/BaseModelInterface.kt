package com.gamapp.domain.models

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
interface BaseTrack : Image, LongId

