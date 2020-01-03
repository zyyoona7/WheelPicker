package com.zyyoona7.picker.interfaces

import com.zyyoona7.picker.ex.WheelDayView
import com.zyyoona7.picker.ex.WheelMonthView
import com.zyyoona7.picker.ex.WheelYearView
import com.zyyoona7.picker.listener.OnDateSelectedListener
import com.zyyoona7.wheel.WheelView
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

    fun setYearRange(startYear: Int, endYear: Int)

    fun setSelectedDate(date: Date)

    fun setSelectedDate(year: Int, month: Int, day: Int)

    fun setMaxSelectedDate(maxDate: Date)

    fun setMaxSelectedDate(maxCalendar: Calendar)

    fun setDateRange(minDate: Date, maxDate: Date)

    fun setDateRange(minCalendar: Calendar, maxCalendar: Calendar)

    fun setLeftText(yearLeft: CharSequence, monthLeft: CharSequence, dayLeft: CharSequence)

    fun setRightText(yearRight: CharSequence, monthRight: CharSequence, dayRight: CharSequence)

    fun setShowYear(isShow: Boolean)

    fun setShowMonth(isShow: Boolean)

    fun setShowDay(isShow: Boolean)

    fun setYearMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setMonthMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setDayMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setMaxTextWidthMeasureType(yearType: WheelView.MeasureType,
                                   monthType: WheelView.MeasureType,
                                   dayType: WheelView.MeasureType)

    fun getSelectedDate(): Date

    /**
     * 选中日期字符串类型 yyyy-M-d
     */
    fun getSelectedDateStr(): String

    fun getSelectedYear(): Int

    fun getSelectedMonth(): Int

    fun getSelectedDay(): Int

    fun getWheelYearView(): WheelYearView

    fun getWheelMonthView(): WheelMonthView

    fun getWheelDayView(): WheelDayView
}