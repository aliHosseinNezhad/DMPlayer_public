package com.gamapp.lib

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout

interface ViewGroupParamScope {
    fun layoutDirection(dir: Int)
    fun matchParentWidth()
    fun matchParentHeight()
    fun matchParentSize() {
        matchParentWidth()
        matchParentHeight()
    }
    fun wrapContentWidth()
    fun wrapContentHeight()
    fun wrapContentSize() {
        wrapContentHeight()
        wrapContentWidth()
    }
    fun width(value: Int)
    fun height(value: Int)
    fun size(value: Int) {
        width(value)
        height(value)
    }
}

class ViewGroupParamScopeImpl(val params: ViewGroup.LayoutParams,val container: ViewGroup.LayoutParams) :
    ViewGroupParamScope {
    override fun layoutDirection(dir: Int) {
        params.resolveLayoutDirection(dir)
        container.resolveLayoutDirection(dir)
    }

    override fun matchParentWidth() {
        params.width = -1
        container.width = -1
    }

    override fun matchParentHeight() {
        params.height = -1
        container.height = -1
    }

    override fun wrapContentWidth() {
        params.width = -2
        container.width = -2
    }

    override fun wrapContentHeight() {
        params.height = -2
        container.height = -2
    }

    override fun width(value: Int) {
        params.width = value
        container.width = -2
    }

    override fun height(value: Int) {
        params.height = value
        container.height = -2
    }
}

interface MarginLayoutParamScope : ViewGroupParamScope {
    fun margin(start: Int = 0, top: Int = 0, bottom: Int = 0, end: Int = 0)
    fun margin(all: Int = 0) =
        margin(all, all, all, all)

    fun margin(horizontal: Int = 0, vertical: Int = 0) =
        margin(start = horizontal, end = horizontal, top = vertical, bottom = vertical)
}

class MarginLayoutParamScopeImpl(
    val params: ViewGroup.MarginLayoutParams,
    val container: ViewGroup.MarginLayoutParams,
) : MarginLayoutParamScope,
    ViewGroupParamScope by ViewGroupParamScopeImpl(params,container) {
    override fun margin(start: Int, top: Int, bottom: Int, end: Int) {
        params.marginStart = start
        params.marginEnd = end
        params.topMargin = top
        params.bottomMargin = bottom
    }
}


interface FrameParamScope : MarginLayoutParamScope {
    fun gravity(gravity: Int)
}

class FrameParamScopeImpl(
    val params: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(0, 0),
    val container: FrameLayout.LayoutParams = FrameLayout.LayoutParams(0, 0),
) : FrameParamScope,
    MarginLayoutParamScope by MarginLayoutParamScopeImpl(params, container) {
    override fun gravity(gravity: Int) {
        container.gravity = gravity
    }
}

interface LinearLayoutParamScope : MarginLayoutParamScope {
    fun weight(value: Float)
    fun gravity(value: Int)
}

class LinearLayoutParamScopeImpl(
    val params: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(0, 0),
    val container: LinearLayout.LayoutParams = LinearLayout.LayoutParams(0, 0),
) : LinearLayoutParamScope,
    MarginLayoutParamScope by MarginLayoutParamScopeImpl(params, container) {
    override fun weight(value: Float) {
        container.weight = value
    }

    override fun gravity(value: Int) {
        container.gravity = value
    }
}


@JvmName("paramsViewGroup")
inline fun <VG:ViewGroup,V : View> Out<ViewGroup, V>.params(scopeLayout: ViewGroupParamScope.() -> Unit): Out<ViewGroup, V> {
    val viewGroupParamScope =
        ViewGroupParamScopeImpl(child.viewGroupParams(),container.viewGroupParams())
    viewGroupParamScope.scopeLayout()
    child.layoutParams = viewGroupParamScope.params
    container.layoutParams = viewGroupParamScope.container
    return this
}
fun View.viewGroupParams():ViewGroup.LayoutParams {
    return (this.layoutParams as? ViewGroup.MarginLayoutParams) ?: ViewGroup.LayoutParams(0,
        0)
}
fun View.params(): ViewGroup.MarginLayoutParams {
    return (this.layoutParams as? ViewGroup.MarginLayoutParams) ?: ViewGroup.MarginLayoutParams(0,
        0)
}

fun View.linearParams(): LinearLayout.LayoutParams {
    return (this.layoutParams as? LinearLayout.LayoutParams) ?: LinearLayout.LayoutParams(0, 0)
}

fun View.frameParams(): FrameLayout.LayoutParams {
    return (this.layoutParams as? FrameLayout.LayoutParams) ?: FrameLayout.LayoutParams(0, 0)
}

@JvmName("paramsFrameLayout")
inline fun <VG : FrameLayout, V : View> Out<VG, V>.params(scopeLayout: FrameParamScope.() -> Unit): Out<VG, V> {
    val frameParamScopeImpl = FrameParamScopeImpl(child.params(), container.frameParams())
    frameParamScopeImpl.scopeLayout()
    child.layoutParams = frameParamScopeImpl.params
    container.layoutParams = frameParamScopeImpl.container
    return this
}

@JvmName("paramsLinearLayout")
inline fun <VG : LinearLayout, V : View> Out<VG, V>.params(scopeLayout: LinearLayoutParamScope.() -> Unit): Out<VG, V> {
    val linearLayoutParamScopeImpl =
        LinearLayoutParamScopeImpl(child.params(), container.linearParams())
    linearLayoutParamScopeImpl.scopeLayout()
    child.layoutParams = linearLayoutParamScopeImpl.params
    container.layoutParams = linearLayoutParamScopeImpl.container
    return this
}