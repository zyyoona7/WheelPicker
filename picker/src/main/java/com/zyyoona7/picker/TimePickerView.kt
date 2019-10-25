package com.zyyoona7.picker

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.zyyoona7.picker.ex.WheelAmPmView
import com.zyyoona7.picker.ex.WheelHourView
import com.zyyoona7.picker.ex.WheelMinuteView
import com.zyyoona7.picker.ex.WheelSecondView
import com.zyyoona7.picker.helper.TimePickerHelper

/**
 * 时间选择器 View
 *
 * @author zyyoona7
 */
class TimePickerView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr) {

    private val timePickerHelper:TimePickerHelper

    init {
        View.inflate(context,R.layout.layout_time_picker_view,this)
        val amPmWheel=findViewById<WheelAmPmView>(R.id.wheel_am_pm)
        val hourWheel=findViewById<WheelHourView>(R.id.wheel_hour)
        val minuteWheel=findViewById<WheelMinuteView>(R.id.wheel_minute)
        val secondWheel=findViewById<WheelSecondView>(R.id.wheel_second)
        timePickerHelper= TimePickerHelper(amPmWheel, hourWheel, minuteWheel, secondWheel)
    }
}