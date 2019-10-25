package com.zyyoona7.picker.helper

import com.zyyoona7.picker.ex.WheelAmPmView
import com.zyyoona7.picker.ex.WheelHourView
import com.zyyoona7.picker.ex.WheelMinuteView
import com.zyyoona7.picker.ex.WheelSecondView
import com.zyyoona7.picker.interfaces.TimePicker
import com.zyyoona7.wheel.WheelView
import com.zyyoona7.wheel.adapter.ArrayWheelAdapter
import com.zyyoona7.wheel.listener.OnItemSelectedListener
import com.zyyoona7.wheel.listener.OnScrollChangedListener

class TimePickerHelper(private var amPmWheel: WheelAmPmView?,
                       private var hourWheel: WheelHourView?,
                       private var minuteWheel: WheelMinuteView?,
                       private var secondWheel: WheelSecondView?)
    : OnItemSelectedListener, OnScrollChangedListener, TimePicker {


    override fun onItemSelected(wheelView: WheelView, adapter: ArrayWheelAdapter<*>, position: Int) {
    }

    override fun onScrollChanged(wheelView: WheelView, scrollOffsetY: Int) {
    }

    override fun onScrollStateChanged(wheelView: WheelView, state: Int) {
    }
}