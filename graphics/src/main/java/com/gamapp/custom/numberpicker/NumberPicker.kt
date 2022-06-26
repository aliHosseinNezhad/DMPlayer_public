package com.gamapp.custom.numberpicker

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.gamapp.custom.ViewNumberPicker

class NumberPicker @SuppressLint("ViewConstructor") constructor(context: Context, val color: Color, val numbers: List<Int>) :
    ViewNumberPicker<NumberPicker.Holder>(context) {
    inner class Holder(private val view: FrameLayout) : ViewNumberPicker.ItemHolder(view) {
        @SuppressLint("SetTextI18n")
        override fun onViewChanged(position: Int) {
            val linearLayout = view.getChildAt(0) as LinearLayout
            (linearLayout.getChildAt(0) as TextView).apply {
                text = numbers[position].toString()
                textSize = 28f
                setTextColor(color.toArgb())
            }
            (linearLayout.getChildAt(2) as TextView).apply {
                text = "min"
                textSize = 10f
                setTextColor(color.toArgb())
            }
        }
    }

    override fun onCreateHolder(
        context: Context,
        parent: ViewGroup
    ): ItemHolder {
        return Holder(FrameLayout(context).apply {
            val view = LinearLayout(context).apply {
                layoutParams = LayoutParams(-2, -1).apply {
                    this.gravity = Gravity.CENTER
                }
                gravity = Gravity.CENTER
                addView(TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(-2, -1)
                    gravity = Gravity.CENTER
                })
                addView(View(context).apply {

                    layoutParams = LayoutParams(16, 0)
                })
                addView(TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(-2, -1)
                    gravity = Gravity.CENTER
                })
            }
            addView(view)
        })
    }

    override fun getCount(): Int {
        return numbers.size
    }
}