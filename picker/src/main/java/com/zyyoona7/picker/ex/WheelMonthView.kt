package com.zyyoona7.picker.ex

import android.content.Context
import android.util.AttributeSet
import com.zyyoona7.picker.R
import com.zyyoona7.picker.interfaces.IndexOfAction
import com.zyyoona7.wheel.WheelView

class WheelMonthView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0)
    : WheelView(context, attrs, defStyleAttr), IndexOfAction<Int> {

    companion object {
        const val MIN_MONTH = 1
        const val MAX_MONTH = 12
    }

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.WheelMonthView)
            val selectedMonth = typedArray.getInt(R.styleable.WheelMonthView_wv_selectedMonth, 1)
            val minSelectedMonth = typedArray.getInt(R.styleable.WheelYearView_wv_minSelectedYear, -1)
            val maxSelectedMonth = typedArray.getInt(R.styleable.WheelMonthView_wv_maxSelectedMonth, -1)
            typedArray.recycle()
            val selectedPosition = indexOf(selectedMonth)
            val maxSelectedPosition = indexOf(maxSelectedMonth)
            val minSelectedPosition = indexOf(minSelectedMonth)
            initSelectedPositionAndRange(selectedPosition, minSelectedPosition, maxSelectedPosition)
        }
        updateMonthData()
    }

    override fun indexOf(item: Int): Int {
        return if (item in MIN_MONTH..MAX_MONTH) {
            item - 1
        } else {
            -1
        }
    }

    override fun getItem(index: Int): Int {
        if (index !in 0 until getItemCount()) {
            return -1
        }
        return index + 1
    }

    private fun updateMonthData() {
        val data = mutableListOf<Int>()
        for (i in MIN_MONTH..MAX_MONTH) {
            data.add(i)
        }
        setData(data)
    }

    @JvmOverloads
    fun setSelectedMonth(month: Int, isSmoothScroll: Boolean = false,
                         smoothDuration: Int = DEFAULT_SCROLL_DURATION) {
        setSelectedPosition(indexOf(month), isSmoothScroll, smoothDuration)
    }

    @JvmOverloads
    fun setSelectedMonthRange(minMonth: Int = MIN_MONTH, maxMonth: Int) {
        setSelectedRange(indexOf(minMonth), indexOf(maxMonth))
    }
}