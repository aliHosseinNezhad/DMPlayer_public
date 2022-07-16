package com.gamapp.dmplayer.presenter.ui.navigation.nav_ext

import android.os.Parcelable


abstract class Route() : Parcelable

inline fun <reified T : Route> T.getKey(): String {
    val kClazz = T::class
    return (kClazz.qualifiedName ?: "")
}

inline fun <reified T : Route> getKey(): String {
    val kClazz = T::class
    return (kClazz.qualifiedName ?: "")
}
