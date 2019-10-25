package com.zyyoona7.picker.ex

import android.content.Context
import android.util.AttributeSet
import com.zyyoona7.picker.R

class WheelMinuteView @JvmOverloads constructor(context: Context,
                                                attrs: AttributeSet? = null,
                                                defStyleAttr: Int = 0)
    : Wheel60View(context, attrs, defStyleAttr) {

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.WheelMinuteView)
            val selectedMinute = typedArray.getInt(R.styleable.WheelMinuteView_wv_selectedMinute, 0)
            val minSelectedMinute = typedArray.getInt(R.styleable.WheelMinuteView_wv_minSelectedMinute, -1)
            val maxSelectedMinute = typedArray.getInt(R.styleable.WheelMinuteView_wv_maxSelectedMinute, -1)
            typedArray.recycle()

            initSelectedPositionAndRange(indexOf(selectedMinute),
                    indexOf(minSelectedMinute), indexOf(maxSelectedMinute))
            updateData()
        }
    }

    @JvmOverloads
    fun setSelectedMinute(minute: Int, isSmoothScroll: Boolean = false,
                          smoothDuration: Int = DEFAULT_SCROLL_DURATION) {
        setSelectedPosition(indexOf(minute), isSmoothScroll, smoothDuration)
    }

    fun setSelectedMinuteRange(minMinute: Int, maxMinute: Int) {
        setSelectedRange(indexOf(minMinute), indexOf(maxMinute))
    }
}