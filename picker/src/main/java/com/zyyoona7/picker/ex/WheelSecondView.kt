package com.zyyoona7.picker.ex

import android.content.Context
import android.util.AttributeSet
import com.zyyoona7.picker.R

class WheelSecondView @JvmOverloads constructor(context: Context,
                                                attrs: AttributeSet? = null,
                                                defStyleAttr: Int = 0)
    : Wheel60View(context, attrs, defStyleAttr) {

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.WheelSecondView)
            val selectedSecond = typedArray.getInt(R.styleable.WheelSecondView_wv_selectedSecond, 0)
            val minSelectedSecond = typedArray.getInt(R.styleable.WheelSecondView_wv_minSelectedSecond, -1)
            val maxSelectedSecond = typedArray.getInt(R.styleable.WheelSecondView_wv_maxSelectedSecond, -1)
            typedArray.recycle()

            initSelectedPositionAndRange(indexFor(selectedSecond),
                    indexFor(minSelectedSecond), indexFor(maxSelectedSecond))
        }
        updateData()
    }

    @JvmOverloads
    fun setSelectedSecond(second: Int, isSmoothScroll: Boolean = false,
                          smoothDuration: Int = DEFAULT_SCROLL_DURATION) {
        setSelectedPosition(indexFor(second), isSmoothScroll, smoothDuration)
    }

    fun setSelectedSecondRange(minSecond: Int, maxSecond: Int) {
        setSelectedRange(indexFor(minSecond), indexFor(maxSecond))
    }
}