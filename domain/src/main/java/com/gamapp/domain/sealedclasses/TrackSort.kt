package com.gamapp.domain.sealedclasses

import com.gamapp.domain.models.TrackModel

 class TrackSort(override val type: Type<TrackModel>, override val order: Order) :
    Sort<TrackModel>() {
    override val types: List<Type<TrackModel>> = listOf(
        SortByDateAdded,
        SortByName
    )

    companion object {
        val SortByName = object : Type<TrackModel>() {
            override fun method(items: List<TrackModel>) = items.sortedBy {
                it.title
            }

            override val name = "Name"
        }

        val SortByDateAdded = object : Type<TrackModel>() {
            override fun method(items: List<TrackModel>) = items.sortedBy {
                it.dateAdded
            }

            override val name: String = "Data Added"

        }
    }

     override fun copy(type: Type<TrackModel>, order: Order): Sort<TrackModel> {
         return TrackSort(type,order)
     }
 }