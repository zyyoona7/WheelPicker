package com.zyyoona7.picker.interfaces

import com.zyyoona7.picker.ex.WheelAmPmView
import com.zyyoona7.picker.ex.WheelHourView
import com.zyyoona7.picker.ex.WheelMinuteView
import com.zyyoona7.picker.ex.WheelSecondView
import com.zyyoona7.wheel.formatter.IntTextFormatter
import com.zyyoona7.wheel.listener.OnScrollChangedListener

interface TimePicker {

    fun setAmPmTextHandler(textHandler: AmPmTextHandler)

    fun setHourTextFormatter(textFormatter: IntTextFormatter)

    fun setMinuteTextFormatter(textFormatter: IntTextFormatter)

    fun setSecondTextFormatter(textFormatter: IntTextFormatter)

    fun setOnScrollChangedListener(listener: OnScrollChangedListener?)

    fun setLeftText(amPmText: CharSequence, hourText: CharSequence,
                    minuteText: CharSequence, secondText: CharSequence)

    fun setRightText(amPmText: CharSequence, hourText: CharSequence,
                     minuteText: CharSequence, secondText: CharSequence)

    fun getWheelAmPmView():WheelAmPmView

    fun getWheelHourView():WheelHourView

    fun getWheelMinuteView():WheelMinuteView

    fun getWheelSecondView():WheelSecondView
}