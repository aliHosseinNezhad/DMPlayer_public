package com.gamapp.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.GestureDetectorCompat
import kotlinx.coroutines.*
import kotlin.math.abs
import kotlin.math.exp


abstract class ViewNumberPicker<holder : ViewNumberPicker.ItemHolder> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : FrameLayout(context, attrs), GestureDetector.OnGestureListener {
    private val fling = CancelableCoroutine(CoroutineScope(Dispatchers.Main + Job()))
    private val visibleCount = 3
    private val horizontal: Boolean = true
    private val frames = generateFrameLayouts()
    private var mDetector: GestureDetectorCompat = GestureDetectorCompat(context, this)
    private val maxIndex get() = getCount() - 1
    private val bound get() = if (horizontal) width else height
    private val ih get() = if (horizontal) height else height / visibleCount
    private val iw get() = if (horizontal) width / visibleCount else width
    private val iBound get() = if (horizontal) iw else ih
    private fun View.scale(value: Float) {
        val scale = value * 1 + (1 - value) * 0.5f
        scaleX = scale
        scaleY = scale
    }

    private val onScrollEnd = CancelableCoroutine(CoroutineScope(Dispatchers.Main + Job()))
    private var setOnSelectedItemChange: ((position: Int) -> Unit)? = null

    var selectedIndex: Int = 0
        set(value) {
            field = value
            setOnSelectedItemChange?.let { it(value) }
        }

    fun setOnSelectedItemChangeListener(listener: (position: Int) -> Unit) {
        setOnSelectedItemChange = listener
    }

    private fun generateFrameLayouts(): MutableList<ItemHolder> {
        val list = mutableListOf<ItemHolder>()
        (0..visibleCount + 1).forEach {
            list += onCreateHolder(context, this)
        }
        return list
    }

    private fun getIndex(size: Int, index: Int): Int {
        if (size < abs(index))
            throw Exception("")
        return if (index < 0) size + index + 1
        else index
    }

    init {
        layoutParams = LayoutParams(-1, -1)
        frames.forEach {
            addView(it.content)
        }
        post {
            setOnSelectedItemChange?.let {
                it(selectedIndex)
            }
            val iterator = frames.iterator()
            var count = -((visibleCount + 2) / 2)
            while (iterator.hasNext()) {
                val item = iterator.next()
                item.content.layoutParams = LayoutParams(iw, ih)
                item.content.setMargin(iBound * (count + visibleCount/2))
                item.index = getIndex(maxIndex,count)
                item.content.alpha = 1f
                count++
            }
            alphaOnViews()
        }
    }


    abstract fun onCreateHolder(context: Context, parent: ViewGroup): ItemHolder
    abstract fun getCount(): Int

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        event.run {
            when (action) {
                MotionEvent.ACTION_UP -> {
                    if (!fling.inProgress) {
                        onScrollEnd()
                    }
                }
            }
        }

        return mDetector.onTouchEvent(event)
    }

    private fun startFling(initialVelocity: Float) {
        fling.start {
            var v = initialVelocity
            var count = 0
            val s = initialVelocity.let { if (it > 0) 1f else -1f }
            while (true) {
                v = (exp(-count / 10000.0) * v).toFloat()
                count++
                delay(1)
                if ((abs(v) * 1000) / bound < 0.15f)
                    break
                scrollViews(-v * 1.5f)
            }
            onScrollEnd()
        }
    }

    fun topView(): ItemHolder? {
        val min = frames.toList().minOf { it.content.margin() }
        val views = frames.toList().filter {
            it.content.margin() == min
        }
        return views.firstOrNull()
    }

    private fun bottomView(): ItemHolder? {
        val max = frames.toList().maxOf { it.content.margin() }
        val views = frames.toList().filter {
            it.content.margin() == max
        }
        return views.firstOrNull()
    }


    private fun printMargins(): String {
        return frames.toList().map { it.content.margin().toString() }.joinToString { it }
    }

    private fun scrollViewI(df: Int) {
        val iterator = frames.iterator()
        val bottomView = bottomView()!!
        val topView = topView()!!
        if (topView.content.margin() + df > -iBound / 2) {
            Log.i("OnRecycle", "scrollViews: top start, ${printMargins()}")
            val value = (topView).index - 1
            (bottomView).index = if (value < 0) maxIndex else value
            bottomView.content.setMargin(topView.content.margin() - iBound)
            Log.i("OnRecycle", "scrollViews: top after, ${printMargins()}")
        } else if (bottomView.content.margin() + iBound <= bound + iBound / 2) {
            Log.i("OnRecycle", "scrollViews: bottom before, ${printMargins()}")
            val value = (bottomView).index + 1
            (topView).index = if (value <= maxIndex) value else 0
            topView.content.setMargin(bottomView.content.margin() + iBound)
            Log.i("OnRecycle", "scrollViews: bottom after, ${printMargins()}")
        }
        while (iterator.hasNext()) {
            val view = iterator.next()
            view.content.setMargin(view.content.margin() + df)
        }
        alphaOnViews()
    }

    private fun scrollViews(_df: Float) {
        val count = _df.toInt() / (iBound / 2)
        val p = _df.toInt() % (iBound / 2)
        scrollViewI(p)
        for (i in 0 until count) {
            scrollViewI(iBound / 2)
        }
    }

    private fun alphaOnViews() {
        val total = (visibleCount+2) * iBound
        val iterator = frames.map { it.content }.iterator()
        while (iterator.hasNext()) {
            val view = iterator.next()
            val dy = bound / 2 - (view.margin() + iBound / 2)
            val alpha = 2 * abs(dy) / (total.toFloat())
            val value = alpha.coerceIn(0f, 1f)
            view.alpha = exp(-value * value * 6f).coerceIn(0.1f, 1f)
            view.scale(view.alpha)
        }
    }


    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent?) {}
    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return true
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float,
    ): Boolean {
        onScrollEnd.stop()
        fling.stop()
        val d = if (horizontal) distanceX else distanceY
        scrollViews(-d)
        return true
    }


    private fun onScrollEnd() {
        val middleView = frames.minByOrNull {
            abs(bound / 2 - (it.content.margin() + iBound / 2))
        }
        middleView?.let { view ->
            val df = bound / 2 - (view.content.margin() + iBound / 2)
            onScrollEnd.start {
                var count = 0
                val s = if (df > 0) 1f else -1f
                while (count < abs(df)) {
                    scrollViews(s * abs(df / 50).coerceAtLeast(1).toFloat())
                    delay(1)
                    count += abs(df / 50).coerceAtLeast(1)
                }
                setOnSelectedItemChange?.let {
                    it(view.index)
                }
            }
        }
    }

    override fun onLongPress(e: MotionEvent?) {

    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float,
    ): Boolean {
        val velocity = if (horizontal) velocityX else velocityY
        if (abs(velocity) / bound.toFloat() >= 0.2f) {
            startFling(-velocity / 1000f)
        }
        return false
    }

    abstract class ItemHolder constructor(
        val content: View,
    ) {
        var index = 0
            set(value) {
                field = value
                onViewChanged(value)
            }

        abstract fun onViewChanged(position: Int)
    }

    private fun View.setMargin(margin: Int) {
        layoutParams = layoutParams.apply {
            this as LayoutParams
            if (horizontal)
                leftMargin = margin
            else topMargin = margin
        }
    }

    private fun View.margin(): Int {
        return (layoutParams as LayoutParams).let { if (horizontal) it.leftMargin else it.topMargin }
    }

}

