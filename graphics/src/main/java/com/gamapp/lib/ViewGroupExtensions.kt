package com.gamapp.lib

import android.view.View
import android.view.ViewGroup

operator fun ViewGroup.plusAssign(view: View) {
    addView(view)
}