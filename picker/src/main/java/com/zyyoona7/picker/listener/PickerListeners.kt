package com.zyyoona7.picker.listener

import android.content.Context
import com.zyyoona7.picker.ex.WheelHourView
import com.zyyoona7.wheel.WheelView
import java.util.*

interface AmPmTextHandler {

    fun amText(context: Context):String

    fun pmText(context: Context):String
}

/**
 * 滚动 WheelHourView 时会触发 上下午 随滚动距离变化
 *
 * @author zyyoona7
 */
interface OnAmPmChangedListener {

    fun onAmPmChanged(wheelHourView: WheelHourView, isAm:Boolean)
}

/**
 * 日期选择回调
 *
 * @author zyyoona7
 */
interface OnDateSelectedListener {

    fun onDateSelected(year: Int, month: Int, day: Int, date: Date)
}

/**
 * 时间选择回调
 *
 * @author zyyoona7
 */
interface OnTimeSelectedListener {

    fun onTimeSelected(is24Hour:Boolean,hour:Int,minute:Int,second:Int,isAm:Boolean)
}

/**
 * 联动选择器选中回调
 *
 * @author zyyoona7
 */
interface OnLinkageSelectedListener {

    fun onLinkageSelected(firstWv: WheelView, secondWv: WheelView?, thirdWv: WheelView?)
}

/**
 * 请求联动2 WheelView 数据
 *
 * @author zyyoona7
 */
interface OnRequestData2Listener {

    fun convert(linkage1Wv: WheelView):List<Any>
}

/**
 * 请求联动3 WheelView 数据
 *
 * @author zyyoona7
 */
interface OnRequestData3Listener {

    fun convert(linkage1Wv:WheelView, linkage2Wv:WheelView):List<Any>
}