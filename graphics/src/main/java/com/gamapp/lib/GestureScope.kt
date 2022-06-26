package com.gamapp.lib

import android.view.View
import android.view.ViewGroup

interface GestureScope {
    fun onClick(onClick: () -> Unit)
    fun onLongClick(onLongClick: () -> Boolean)
}

class GestureScopeImpl(private val view: View) : GestureScope {
    override fun onClick(onClick: () -> Unit) {
        view.isClickable = true
        view.isFocusable = true
        view.setOnClickListener {
            onClick()
        }
    }
    override fun onLongClick(onLongClick: () -> Boolean) {
        view.isLongClickable = true
        view.isFocusable = true
        view.setOnLongClickListener {
            onLongClick()
        }
    }

}

inline fun <VM : ViewGroup, V : View> Out<VM, V>.gesture(gestureScope: GestureScope.() -> Unit): Out<VM, V> {
    val gestureScopeImpl = GestureScopeImpl(container)
    gestureScopeImpl.gestureScope()
    return this
}