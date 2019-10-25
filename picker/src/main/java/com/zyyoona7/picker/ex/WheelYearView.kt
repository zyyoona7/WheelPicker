package com.zyyoona7.picker.ex

import android.content.Context
import android.util.AttributeSet
import com.zyyoona7.picker.R
import com.zyyoona7.picker.interfaces.IndexOfAction
import com.zyyoona7.wheel.WheelView

class WheelYearView @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0)
    : WheelView(context, attrs, defStyleAttr), IndexOfAction<Int> {

    private var startYear = 1970
    private var endYear = 2100

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.WheelYearView)
            startYear = typedArray.getInt(R.styleable.WheelYearView_wv_startYear, 2000)
            endYear = typedArray.getInt(R.styleable.WheelYearView_wv_endYear, 2100)
            val selectedYear = typedArray.getInt(R.styleable.WheelYearView_wv_selectedYear, 2000)
            val maxSelectedYear = typedArray.getInt(R.styleable.WheelYearView_wv_maxSelectedYear, -1)
            val minSelectedYear = typedArray.getInt(R.styleable.WheelYearView_wv_minSelectedYear, -1)
            typedArray.recycle()

            val selectedPosition = indexOf(selectedYear)
            val maxSelectedPosition = indexOf(maxSelectedYear)
            val minSelectedPosition = indexOf(minSelectedYear)
            initSelectedPositionAndRange(selectedPosition, minSelectedPosition, maxSelectedPosition)

            updateYearData()
        }
    }

    override fun indexOf(item: Int): Int {
        return if (item in startYear..endYear) {
            item - startYear
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

    private fun updateYearData() {
        val data: MutableList<Int> = arrayListOf()
        for (i in startYear..endYear) {
            data.add(i)
        }
        setData(data)
    }

    /**
     * 设置年份取值范围
     */
    fun setYearRange(start: Int, end: Int) {
        require(start >= end) {
            "endYear must be greater than startYear in WheelYearView."
        }

        startYear = start
        endYear = end
        updateYearData()
    }

    /**
     * 设置当前选中的年份
     */
    @JvmOverloads
    fun setSelectedYear(year: Int, isSmoothScroll: Boolean = false,
                        smoothDuration: Int = DEFAULT_SCROLL_DURATION) {
        setSelectedPosition(indexOf(year), isSmoothScroll, smoothDuration)
    }

    /**
     * 设置可以选中的年份范围
     */
    @JvmOverloads
    fun setSelectedYearRange(minYear: Int = startYear, maxYear: Int) {
        setSelectedRange(indexOf(minYear), indexOf(maxYear))
    }
}