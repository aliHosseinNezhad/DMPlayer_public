package com.gamapp.domain.sealedclasses

import com.gamapp.domain.models.ArtistModel

class ArtistSort(
    override val type: Type<ArtistModel>, override val order: Order,
) : Sort<ArtistModel>() {
    companion object {
        val SortByDataAdded = object : Type<ArtistModel>() {
            override fun method(items: List<ArtistModel>) = items.sortedBy {
                it.dateAdded
            }

            override val name: String = "Date Added"
        }
        val SortByName = object : Type<ArtistModel>() {
            override fun method(items: List<ArtistModel>) = items.sortedBy {
                it.title
            }

            override val name: String = "Name"
        }
        val SortByTracksCount = object : Type<ArtistModel>() {
            override fun method(items: List<ArtistModel>) = items.sortedBy {
                it.count
            }

            override val name: String = "Tracks Count"
        }
        val types: List<Type<ArtistModel>> = listOf(
            SortByDataAdded,
            SortByName,
            SortByTracksCount
        )
    }

    override val types: List<Type<ArtistModel>> = listOf(
        SortByDataAdded,
        SortByName,
        SortByTracksCount
    )

    override fun copy(type: Type<ArtistModel>, order: Order): Sort<ArtistModel> {
        return ArtistSort(type,order)
    }
}