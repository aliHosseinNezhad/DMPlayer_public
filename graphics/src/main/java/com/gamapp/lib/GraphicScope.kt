package com.gamapp.lib


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.*
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.graphics.*
import kotlin.math.roundToInt

fun Rect.rounded(radius: Float): Path {
    val path = Path()
    path.addRoundRect(this.toRectF(), radius, radius, Path.Direction.CCW)
    return path
}

fun Rect.rounded(
    radiusTopLeft: Float,
    radiusTopRight: Float,
    radiusBottomLeft: Float,
    radiusBottomRight: Float,
): Path {
    val path = Path()
    path.moveTo(this.left + radiusTopLeft, this.top.toFloat())
    path.lineTo(this.right - radiusTopRight, this.top.toFloat())
    if (radiusTopRight > 0f)
        path.arcTo(
            this.right - 2f * radiusTopRight,
            this.top - 0f,
            this.right - 0f,
            this.top + 2f * radiusTopRight,
            -90f,
            90f,
            false
        )
    path.lineTo(this.right.toFloat(), this.bottom - radiusBottomRight)
    if (radiusBottomRight > 0f)
        path.arcTo(
            this.right - 2f * radiusBottomRight,
            this.bottom - 2f * radiusBottomRight,
            this.right - 0f,
            this.bottom - 0f,
            0f,
            90f,
            false
        )
    path.lineTo(this.left.toFloat() + radiusBottomLeft, this.bottom.toFloat())
    if (radiusBottomLeft > 0f)
        path.arcTo(
            this.left - 0f,
            this.bottom - 2f * radiusBottomLeft,
            this.left + 2 * radiusBottomLeft,
            this.bottom - 0f,
            90f,
            90f,
            false
        )
    path.lineTo(this.left.toFloat(), this.top - radiusTopLeft)
    if (radiusTopLeft > 0f)
        path.arcTo(
            this.left - 0f,
            this.top - 0f,
            this.left + 2 * radiusTopLeft,
            this.top + 2 * radiusTopLeft,
            180f,
            90f,
            false
        )
    path.close()
    return path
}


val Float.color: Int get() = (this * 255).roundToInt()
fun Int.copy(
    red: Int = this.red,
    green: Int = this.green,
    blue: Int = this.blue,
    alpha: Int = this.alpha,
): Int {
    return Color.argb(alpha, red, green, blue)
}

interface GraphicScope {
    var backColor: Int
    fun ripple(@ColorInt rippleColor: Int, content: Drawable): Drawable
    fun background(drawable: Drawable, rippleColor: Int? = null): Drawable
    fun background(shape: (Rect) -> Path, color: Int, rippleColor: Int? = null): Drawable

    @RequiresApi(Build.VERSION_CODES.M)
    fun foreground(shape: (Rect) -> Path, color: Int, rippleColor: Int? = null): Drawable

    @RequiresApi(Build.VERSION_CODES.M)
    fun foreground(drawable: Drawable, color: Int, rippleColor: Int?): Drawable

    fun clip(shape: ContainerView.(Rect) -> Path)

    fun Rect.rounded(
        radiusTopLeft: Float = 0f,
        radiusTopRight: Float = 0f,
        radiusBottomLeft: Float = 0f,
        radiusBottomRight: Float = 0f,
    ): Path

    fun Rect.rounded(
        radius: Float,
    ): Path
}

class GraphicScopeImpl(private val view: ContainerView? = null) : GraphicScope {
    override fun ripple(
        @ColorInt rippleColor: Int,
        content: Drawable,
    ): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RippleDrawable(
                ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_pressed),
                        intArrayOf(android.R.attr.state_focused),
                        intArrayOf(android.R.attr.state_activated)
                    ),
                    intArrayOf(
                        rippleColor,
                        rippleColor,
                        rippleColor
                    )
                ),
                content,
                null
            )
        } else {
            content
        }
    }

    override var backColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            view?.setBackgroundColor(value)
        }

    override fun background(drawable: Drawable, rippleColor: Int?): Drawable {
        val content = if (rippleColor != null)
            ripple(rippleColor = rippleColor, content = drawable)
        else drawable
        view?.background = content
        return content
    }

    override fun background(shape: (Rect) -> Path, color: Int, rippleColor: Int?): Drawable {
        val content = object : Drawable() {
            val rect = Rect()
            override fun draw(canvas: Canvas) {
                canvas.getClipBounds(rect)
                canvas.clipPath(shape(rect))
                canvas.drawColor(color.copy(alpha = (color.alpha / 255f * this.alpha / 255f).roundToInt() * 255))
            }

            override fun setAlpha(alpha: Int) {
                invalidateSelf()
            }

            override fun setColorFilter(colorFilter: ColorFilter?) {
                invalidateSelf()
            }

            override fun getOpacity(): Int {
                return PixelFormat.OPAQUE
            }
        }
        return background(drawable = content, rippleColor = rippleColor)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun foreground(shape: (Rect) -> Path, color: Int, rippleColor: Int?): Drawable {
        val content = object : Drawable() {
            val rect = Rect()
            override fun draw(canvas: Canvas) {
                canvas.getClipBounds(rect)
                canvas.clipPath(shape(rect))
                canvas.drawColor(color.copy(alpha = (color.alpha / 255f * this.alpha / 255f).roundToInt() * 255))
            }

            override fun setAlpha(alpha: Int) {
                invalidateSelf()
            }

            override fun setColorFilter(colorFilter: ColorFilter?) {
                invalidateSelf()
            }

            override fun getOpacity(): Int {
                return PixelFormat.OPAQUE
            }
        }
        return foreground(drawable = content, color, rippleColor = rippleColor)
    }

    override fun Rect.rounded(radius: Float): Path {
        val path = Path()
        path.addRoundRect(this.toRectF(), radius, radius, Path.Direction.CCW)
        return path
    }

    override fun Rect.rounded(
        radiusTopLeft: Float,
        radiusTopRight: Float,
        radiusBottomLeft: Float,
        radiusBottomRight: Float,
    ): Path {
        val path = Path()
        path.moveTo(this.left + radiusTopLeft, this.top.toFloat())
        path.lineTo(this.right - radiusTopRight, this.top.toFloat())
        if (radiusTopRight > 0f)
            path.arcTo(
                this.right - 2f * radiusTopRight,
                this.top - 0f,
                this.right - 0f,
                this.top + 2f * radiusTopRight,
                -90f,
                90f,
                false
            )
        path.lineTo(this.right.toFloat(), this.bottom - radiusBottomRight)
        if (radiusBottomRight > 0f)
            path.arcTo(
                this.right - 2f * radiusBottomRight,
                this.bottom - 2f * radiusBottomRight,
                this.right - 0f,
                this.bottom - 0f,
                0f,
                90f,
                false
            )
        path.lineTo(this.left.toFloat() + radiusBottomLeft, this.bottom.toFloat())
        if (radiusBottomLeft > 0f)
            path.arcTo(
                this.left - 0f,
                this.bottom - 2f * radiusBottomLeft,
                this.left + 2 * radiusBottomLeft,
                this.bottom - 0f,
                90f,
                90f,
                false
            )
        path.lineTo(this.left.toFloat(), this.top - radiusTopLeft)
        if (radiusTopLeft > 0f)
            path.arcTo(
                this.left - 0f,
                this.top - 0f,
                this.left + 2 * radiusTopLeft,
                this.top + 2 * radiusTopLeft,
                180f,
                90f,
                false
            )
        path.close()
        return path
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun foreground(drawable: Drawable, color: Int, rippleColor: Int?): Drawable {
        val content = if (rippleColor != null)
            ripple(rippleColor = rippleColor, content = drawable)
        else drawable
        view?.foreground = content
        return content
    }

    override fun clip(shape: ContainerView.(Rect) -> Path) {
        view?.let { v ->
//            v.setOnClip {
//                v.shape(it)
//            }
        }
    }


}


inline fun <VM : ViewGroup, V : View> Out<VM, V>.graphic(graphicScope: GraphicScope.() -> Unit): Out<VM, V> {
    GraphicScopeImpl(container).graphicScope()
    return this
}

fun <VM : ViewGroup, V : View> Out<VM, V>.clip(shape: (Rect) -> Path): Out<VM, V> {
    if (child is Clipable) {
        child.setOnClip(shape)
    }
    return this
}

interface Clipable {
    fun setOnClip(clip: (Rect) -> Path)
}

interface ClipableInternal : Clipable {
    fun start(view: View)
    fun clip(canvas: Canvas?)
}

class ClipableImpl(val onSet: () -> Unit) : ClipableInternal {
    private var view: View? = null
    private var onClipPath: ((Rect) -> Path)? = null
    private val rect: Rect = Rect()
    override fun setOnClip(clip: (Rect) -> Path) {
        onClipPath = clip
        onSet()
        view?.invalidateOutline()
        view?.invalidate()
    }

    override fun start(view: View) {
        this.view = view
    }

    override fun clip(canvas: Canvas?) {
        canvas?.getClipBounds(rect)
        onClipPath?.let { path ->
            canvas?.clipPath(path(rect))
        }
    }
}

class ContainerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {
    companion object {
        const val TAG = "ContainerView"
    }

    private var clipPath : ((Rect) -> Path)? = null

    fun setOnClip(path:(Rect)->Path){
        clipPath = path
    }
    private fun onClip(canvas: Canvas?){
        canvas?.run {
            getClipBounds(rect)
            clipPath?.let {
                clipPath(it(rect))
            }
        }
    }
    override fun onDrawForeground(canvas: Canvas?) {
        onClip(canvas)
        super.onDrawForeground(canvas)
    }

    override fun onDraw(canvas: Canvas?) {
        onClip(canvas)
        super.onDraw(canvas)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        onClip(canvas)
        super.dispatchDraw(canvas)
    }

    var enabledTouch: Boolean = true

    private val rect = Rect()

//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        return if (clipArea != null)
//            true
//        else
//            super.onTouchEvent(event)
//    }
//
//    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        return if (clipArea != null)
//            true
//        else
//            super.onInterceptTouchEvent(ev)
//    }
}
