package com.zyyoona7.picker.ex

import android.content.Context
import android.util.AttributeSet
import com.zyyoona7.picker.R
import com.zyyoona7.picker.interfaces.IndexOfAction
import com.zyyoona7.wheel.WheelView

class WheelHourView @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0)
    : WheelView(context, attrs, defStyleAttr), IndexOfAction<Int> {

    var is24Hour: Boolean = true
        set(value) {
            if (value == field) {
                return
            }
            field = value
            updateHourData()
        }
    private var hourRange: IntRange = 0 until 24

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.WheelHourView)
            is24Hour = typedArray.getBoolean(R.styleable.WheelHourView_wv_is24Hour, true)
            val selectedHour = typedArray.getInt(R.styleable.WheelHourView_wv_selectedHour, 1)
            val minSelectedHour = typedArray.getInt(R.styleable.WheelHourView_wv_minSelectedHour, -1)
            val maxSelectedHour = typedArray.getInt(R.styleable.WheelHourView_wv_maxSelectedHour, -1)
            typedArray.recycle()

            initSelectedPositionAndRange(indexOf(selectedHour),
                    indexOf(minSelectedHour), indexOf(maxSelectedHour))

            updateHourData()
        }
    }

    override fun indexOf(item: Int): Int {
        return if (item in hourRange) {
            if (is24Hour) item else item - 1
        } else {
            -1
        }
    }

    override fun getItem(index: Int): Int {
        if (index !in 0 until getItemCount()) {
            return -1
        }
        return getAdapter()?.getItem<Int>(index) ?: -1
    }

    private fun updateHourData() {
        val dataList = mutableListOf<Int>()
        updateHourRange()
        for (hour in hourRange) {
            dataList.add(hour)
        }
        setData(dataList)
    }

    private fun updateHourRange() {
        hourRange = if (is24Hour) 0 until 24 else 1..12
    }

    @JvmOverloads
    fun setSelectedHour(hour: Int, isSmoothScroll: Boolean = false,
                        smoothDuration: Int = DEFAULT_SCROLL_DURATION) {
        setSelectedPosition(indexOf(hour), isSmoothScroll, smoothDuration)
    }

    fun setSelectedHourRange(minHour: Int, maxHour: Int) {
        setSelectedRange(indexOf(minHour), indexOf(maxHour))
    }
}