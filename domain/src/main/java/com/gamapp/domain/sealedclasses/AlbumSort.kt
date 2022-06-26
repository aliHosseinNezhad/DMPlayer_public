package com.gamapp.domain.sealedclasses

import com.gamapp.domain.models.AlbumModel

 class AlbumSort(override val type: Type<AlbumModel>, override val order: Order) :
    Sort<AlbumModel>() {
    companion object {
        val SortByDataAdded = object : Type<AlbumModel>() {
            override fun method(items: List<AlbumModel>) = items.sortedBy {
                it.dateAdded
            }

            override val name: String = "Date Added"
        }
        val SortByName = object : Type<AlbumModel>() {
            override fun method(items: List<AlbumModel>) = items.sortedBy {
                it.title
            }

            override val name: String = "Name"
        }
        val SortByTracksCount = object : Type<AlbumModel>() {
            override fun method(items: List<AlbumModel>) = items.sortedBy {
                it.count
            }

            override val name: String = "Tracks Count"
        }
        val types: List<Type<AlbumModel>> = listOf(
            SortByDataAdded,
            SortByName,
            SortByTracksCount
        )
    }

    override val types: List<Type<AlbumModel>> = listOf(
        SortByDataAdded,
        SortByName,
        SortByTracksCount
    )

     override fun copy(type: Type<AlbumModel>, order: Order): Sort<AlbumModel> {
         return AlbumSort(type,order)
     }
 }
