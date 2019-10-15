package com.zyyoona7.picker.ex

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import com.zyyoona7.picker.R
import com.zyyoona7.picker.interfaces.IndexOfAction
import com.zyyoona7.wheel.WheelViewKt
import java.util.*
import kotlin.math.max
import kotlin.math.min

class WheelDayView @JvmOverloads constructor(context: Context,
                                             attrs: AttributeSet? = null,
                                             defStyleAttr: Int = 0)
    : WheelViewKt(context, attrs, defStyleAttr), IndexOfAction<Int> {

    var year: Int = -1
        set(value) {
            if (value == field) {
                return
            }
            field = max(value, 0)
            updateDayData()
        }

    var month: Int = -1
        set(value) {
            if (value == field) {
                return
            }
            field = min(12, max(0, value))
            updateDayData()
        }
    private var selectedDay = 1
    private var minSelectedDay: Int = -1
    private var maxSelectedDay: Int = -1
    private val daysArray: SparseArray<List<Int>> by lazy { SparseArray<List<Int>>() }

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WheelDayView)
            year = typedArray.getInt(R.styleable.WheelDayView_wv_year, 2019)
            month = typedArray.getInt(R.styleable.WheelDayView_wv_month, 1)
            selectedDay = typedArray.getInt(R.styleable.WheelDayView_wv_selectedDay, 1)
            minSelectedDay = typedArray.getInt(R.styleable.WheelDayView_wv_minSelectedDay, -1)
            maxSelectedDay = typedArray.getInt(R.styleable.WheelDayView_wv_maxSelectedDay, -1)
            typedArray.recycle()

            val selectedPosition = indexOf(selectedDay)
            val maxSelectedPosition = indexOf(maxSelectedDay)
            val minSelectedPosition = indexOf(minSelectedDay)
            initSelectedPositionAndRange(selectedPosition, minSelectedPosition, maxSelectedPosition)

            updateDayData()
        }
    }

    override fun indexOf(item: Int): Int {
        val days = getDaysByCalendar()
        return if (item in 1..days) {
            item - 1
        } else {
            -1
        }
    }

    override fun getItem(index: Int): Int? {
        return if (index in 0 until getItemCount()) {
            index + 1
        } else {
            -1
        }
    }

    private fun updateDayData() {
        val days = getDaysByCalendar()
        //天数变化时才更新Adapter
        if (days == getItemCount()) {
            return
        }
        setData(daysArray.get(days) ?: generateDays(days))
    }

    private fun getDaysByCalendar(): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DATE, 1)
        calendar.roll(Calendar.DATE, -1)

        return calendar.get(Calendar.DATE)
    }

    private fun generateDays(days: Int): List<Int> {
        val daysList = mutableListOf<Int>()
        for (i in 1..days) {
            daysList.add(i)
        }
        daysArray.put(days, daysList)
        return daysList
    }

    fun setYearAndMonth(year: Int, month: Int) {
        this.year = year
        this.month = month

        updateDayData()
    }

    fun setDate(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        updateDayData()
        setSelectedDay(currentDay)
    }

    fun getDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, getSelectedItem<Int>() ?: 1)
        return calendar.time
    }

    @JvmOverloads
    fun setSelectedDay(dayOfMonth: Int, isSmoothScroll: Boolean = false,
                       smoothDuration: Int = DEFAULT_SCROLL_DURATION) {
        setSelectedPosition(indexOf(dayOfMonth), isSmoothScroll, smoothDuration)
    }

    @JvmOverloads
    fun setSelectedDayRange(min: Int = 1, max: Int) {
        setSelectedRange(indexOf(min), indexOf(max))
    }
}