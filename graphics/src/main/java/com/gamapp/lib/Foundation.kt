package com.gamapp.lib


import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData

interface MetricsScope {
    val Float.dp: Float
    val Int.sp: Float
    val Int.dp: Int
    val Float.toPx: Float
    val Int.toPx: Int
    val Float.sp: Float
}

abstract class BaseActivity : ComponentActivity(), MetricsScope {
    interface UpdateScope<V : View> {
        val <T> LiveData<T>.get: T?
        fun <T> set(liveData: LiveData<T>, scope: V.(T?) -> Unit)
    }

    class UpdateScopeImpl<V : View>(private val view: V) : UpdateScope<V> {
        class UpdateState<T, V : View>(val t: LiveData<T>, val scope: V.(T?) -> Unit)

        val list = mutableListOf<UpdateState<Any, View>>()
        override val <T> LiveData<T>.get: T?
            get() = run {
                this.value
            }

        override fun <T> set(liveData: LiveData<T>, scope: V.(T?) -> Unit) {
            val updateState = UpdateState(liveData, scope)
            list.add(updateState as UpdateState<Any, View>)
            view.scope(liveData.value)
        }
    }

    fun <V : View> V.update(content: UpdateScope<V>.(V) -> Unit) {
        val updateScopeImpl = UpdateScopeImpl(this)
        updateScopeImpl.content(this)
        for (updateState in updateScopeImpl.list) {
            updateState.t.observe(this@BaseActivity) {
                updateState.scope(this, it)
            }
        }
    }

    fun <T> LiveData<T>.update(content: (T?) -> Unit) {
        observe(this@BaseActivity) {
            content(it)
        }
    }
//    operator fun <T> MutableLiveData<T>.


    override val Float.sp: Float
        get() = run {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                this,
                resources.displayMetrics
            )
        }
    override val Int.sp: Float
        get() = this.toFloat().sp
    override val Float.dp get() = run { resources.displayMetrics.density * this }
    override val Int.dp get() = run { resources.displayMetrics.density * this }.toInt()
    override val Float.toPx get() = run { this / resources.displayMetrics.density }
    override val Int.toPx get() = run { this / resources.displayMetrics.density }.toInt()
}

class Root<VG : ViewGroup>(val viewGroup: VG, val container: ContainerView)

class Out<VG : ViewGroup, V : View>(val parent: VG, val child: V, val container: ContainerView)

inline fun <reified VG : ViewGroup, reified V : View> VG.child(content: VG.() -> V): Out<VG, V> {
    val child = content()
    val container = ContainerView(context).set {
        addView(child)
        layoutParams = ViewGroup.LayoutParams(-2, -2)
    }
    addView(container)
    return Out(this, child, container)
}

inline fun <reified VG : View> VG.set(content: VG.() -> Unit): VG {
    content()
    return this
}




inline fun <reified VG : ViewGroup> VG.root(content: VG.() -> Unit): Root<VG> {
    return Root(this, ContainerView(context).apply {
        addView(this@root)
        content()
        layoutParams = ViewGroup.LayoutParams(-1, -1)
    })
}


fun Context.root(scope: FrameLayout.() -> Unit): FrameLayout {
    val layout = FrameLayout(this).apply {
        layoutParams = ViewGroup.LayoutParams(-1, -1)
    }
    layout.scope()
    return layout
}