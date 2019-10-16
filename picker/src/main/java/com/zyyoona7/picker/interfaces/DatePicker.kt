package com.zyyoona7.picker.interfaces

import com.zyyoona7.picker.ex.WheelDayView
import com.zyyoona7.picker.ex.WheelMonthView
import com.zyyoona7.picker.ex.WheelYearView
import com.zyyoona7.picker.listener.OnDateSelectedListener
import com.zyyoona7.wheel.formatter.IntTextFormatter
import com.zyyoona7.wheel.listener.OnScrollChangedListener
import java.util.*

/**
 * 日期选择类需要实现的接口
 *
 * @author zyyoona7
 */
interface DatePicker {

    fun setYearTextFormatter(textFormatter: IntTextFormatter)

    fun setMonthTextFormatter(textFormatter: IntTextFormatter)

    fun setDayTextFormatter(textFormatter: IntTextFormatter)

    fun setOnDateSelectedListener(listener: OnDateSelectedListener?)

    fun setOnScrollChangedListener(listener: OnScrollChangedListener?)

    fun setSelectedDate(date: Date)

    fun setSelectedDate(year: Int, month: Int, day: Int)

    fun setDateRange(minDate: Date, maxDate: Date)

    fun setDateRange(minCalendar: Calendar, maxCalendar: Calendar)

    fun getSelectedDate(): Date

    /**
     * 选中日期字符串类型 yyyy-M-d
     */
    fun getSelectedDateStr():String

    fun getSelectedYear(): Int

    fun getSelectedMonth(): Int

    fun getSelectedDay(): Int

    fun getWheelYearView(): WheelYearView

    fun getWheelMonthView(): WheelMonthView

    fun getWheelDayView(): WheelDayView
}