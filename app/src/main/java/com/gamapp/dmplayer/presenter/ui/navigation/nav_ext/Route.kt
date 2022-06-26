package com.gamapp.dmplayer.presenter.ui.navigation.nav_ext

import android.os.Parcelable


abstract class Route(open val key: String = "") : Parcelable

inline fun <reified T : Route> T.getQualifiedName(): String {
    val kClazz = T::class
    return ((kClazz.qualifiedName ?: "") + key)
}

//inline fun <reified T : Route> T.getKey(): Pair<String, Route> {
//    val key = getQualifiedName()
//    return key to this
//}

inline fun <reified T : Route> getQualifiedName(key: String): String {
    val kClazz = T::class
    return (kClazz.qualifiedName ?: "") + key
}