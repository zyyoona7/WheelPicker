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

    fun setShowHour(isShow:Boolean)

    fun setShowMinute(isShow:Boolean)

    fun setShowSecond(isShow:Boolean)

    fun setAmPmMaxTextWidthMeasureType(@WheelView.MeasureType measureType: Int)

    fun setHourMaxTextWidthMeasureType(@WheelView.MeasureType measureType:Int)

    fun setMinuteMaxTextWidthMeasureType(@WheelView.MeasureType measureType: Int)

    fun setSecondMaxTextWidthMeasureType(@WheelView.MeasureType measureType: Int)

    fun setMaxTextWidthMeasureType(@WheelView.MeasureType measureType: Int)

    fun setMaxTextWidthMeasureType(@WheelView.MeasureType amPmType:Int,
                                   @WheelView.MeasureType hourType:Int,
                                   @WheelView.MeasureType minuteType:Int,
                                   @WheelView.MeasureType secondType:Int)

    fun getWheelAmPmView(): WheelAmPmView

    fun getWheelHourView(): WheelHourView

    fun getWheelMinuteView(): WheelMinuteView

    fun getWheelSecondView(): WheelSecondView
}