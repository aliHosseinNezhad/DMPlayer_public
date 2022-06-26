package com.gamapp.domain.sealedclasses

import com.gamapp.domain.models.ImagedItemModel

fun <T : ImagedItemModel> List<T>.reversBy(order: Order): List<T> {
    return if (order == Order.STE) this
    else this.reversed()
}

fun Order.switch(): Order {
    return if (this == Order.STE) Order.ETS else Order.STE
}

enum class Order {
    STE,
    ETS
}

abstract class Sort<T : ImagedItemModel> {
    abstract class Type<T : ImagedItemModel> {
        abstract fun method(items: List<T>): List<T>
        abstract val name: String
    }
    abstract val order: Order
    abstract val type: Type<T>
    abstract val types: List<Type<T>>
    abstract fun copy(type: Type<T>,order: Order):Sort<T>
    fun method(items: List<T>): List<T> = type.method(items).reversBy(order)
}


fun main() {

    val albumSort = AlbumSort(type = AlbumSort.SortByDataAdded, order = Order.STE)
    val artistSort = ArtistSort(type = ArtistSort.SortByDataAdded, order = Order.ETS)
}