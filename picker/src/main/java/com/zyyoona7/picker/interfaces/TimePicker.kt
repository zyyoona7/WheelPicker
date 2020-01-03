package com.zyyoona7.picker.interfaces

import com.zyyoona7.picker.ex.WheelAmPmView
import com.zyyoona7.picker.ex.WheelHourView
import com.zyyoona7.picker.ex.WheelMinuteView
import com.zyyoona7.picker.ex.WheelSecondView
import com.zyyoona7.picker.listener.OnTimeSelectedListener
import com.zyyoona7.wheel.WheelView
import com.zyyoona7.wheel.formatter.IntTextFormatter
import com.zyyoona7.wheel.listener.OnScrollChangedListener
import java.util.*

interface TimePicker {

    fun setAmPmTextHandler(textHandler: AmPmTextHandler)

    fun setHourTextFormatter(textFormatter: IntTextFormatter)

    fun setMinuteTextFormatter(textFormatter: IntTextFormatter)

    fun setSecondTextFormatter(textFormatter: IntTextFormatter)

    fun setOnScrollChangedListener(listener: OnScrollChangedListener?)

    fun setOnTimeSelectedListener(listener: OnTimeSelectedListener?)

    fun setLeftText(amPmText: CharSequence, hourText: CharSequence,
                    minuteText: CharSequence, secondText: CharSequence)

    fun setRightText(amPmText: CharSequence, hourText: CharSequence,
                     minuteText: CharSequence, secondText: CharSequence)

    fun set24Hour(is24Hour: Boolean)

    /**
     * 设置选中时间
     */
    fun setTime(calendar: Calendar, is24Hour: Boolean)

    /**
     * 设置选中时间 24小时制
     */
    fun setTime(hour: Int, minute: Int, second: Int)

    /**
     * 设置选中时间 12小时制
     */
    fun setTime(hour: Int, minute: Int, second: Int, isAm: Boolean)

    fun setShowHour(isShow: Boolean)

    fun setShowMinute(isShow: Boolean)

    fun setShowSecond(isShow: Boolean)

    fun setAmPmMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setHourMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setMinuteMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setSecondMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setMaxTextWidthMeasureType(amPmType: WheelView.MeasureType,
                                   hourType: WheelView.MeasureType,
                                   minuteType: WheelView.MeasureType,
                                   secondType: WheelView.MeasureType)

    fun getWheelAmPmView(): WheelAmPmView

    fun getWheelHourView(): WheelHourView

    fun getWheelMinuteView(): WheelMinuteView

    fun getWheelSecondView(): WheelSecondView
}