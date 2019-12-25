package com.zyyoona7.picker.listener

import com.zyyoona7.picker.ex.WheelHourView

/**
 * 滚动 WheelHourView 时会触发 上下午 随滚动距离变化
 *
 * @author zyyoona7
 */
interface OnAmPmChangedListener {

    fun onAmPmChanged(wheelHourView: WheelHourView,isAm:Boolean)
}