package com.zyyoona7.wheel.listener

import com.zyyoona7.wheel.WheelViewKt
import com.zyyoona7.wheel.adapter.ArrayWheelAdapter

/**
 * WheelView 滚动停止后的选中监听器
 */
interface OnItemSelectedListener {

    fun onItemSelected(wheelView:WheelViewKt, adapter: ArrayWheelAdapter<*>,position:Int)
}