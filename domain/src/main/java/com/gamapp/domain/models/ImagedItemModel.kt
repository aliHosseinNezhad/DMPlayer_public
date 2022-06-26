package com.gamapp.domain.models

interface ImagedItemModel : TitledModel {
    val imageId: Long
    val defaultImage: Int
}

interface LongIdModel : TitledModel {
    override val id: Long
}

interface TitledModel {
    val id: Any
    val title: String
    val subtitle: String
}