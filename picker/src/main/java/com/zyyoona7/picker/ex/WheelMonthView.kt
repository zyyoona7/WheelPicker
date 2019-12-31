package com.zyyoona7.picker.ex

import android.content.Context
import android.util.AttributeSet
import com.zyyoona7.picker.R

class WheelMonthView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0)
    : WheelIntView(context, attrs, defStyleAttr) {

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
            val selectedPosition = indexFor(selectedMonth)
            val maxSelectedPosition = indexFor(maxSelectedMonth)
            val minSelectedPosition = indexFor(minSelectedMonth)
            initSelectedPositionAndRange(selectedPosition, minSelectedPosition, maxSelectedPosition)
        }
        updateMonthData()
    }

    override fun indexFor(item: Int): Int {
        return if (item in MIN_MONTH..MAX_MONTH) {
            item - 1
        } else {
            -1
        }
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
        setSelectedPosition(indexFor(month), isSmoothScroll, smoothDuration)
    }

    @JvmOverloads
    fun setSelectedMonthRange(minMonth: Int = MIN_MONTH, maxMonth: Int) {
        setSelectedRange(indexFor(minMonth), indexFor(maxMonth))
    }
}