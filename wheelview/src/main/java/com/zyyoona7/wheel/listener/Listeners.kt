package com.zyyoona7.wheel.listener

import com.zyyoona7.wheel.WheelView
import com.zyyoona7.wheel.adapter.ArrayWheelAdapter

/**
 * WheelView 滚动停止后的选中监听器
 *
 * @author zyyoona7
 */
interface OnItemSelectedListener {

    fun onItemSelected(wheelView: WheelView, adapter: ArrayWheelAdapter<*>, position: Int)
}


/**
 * WheelView 滚动时 position 变化监听器
 *
 * @author zyyoona7
 */
interface OnItemPositionChangedListener {

    fun onItemChanged(wheelView: WheelView, oldPosition: Int, newPosition: Int)
}


/**
 * WheelView 滚动变化监听器
 *
 * @author zyyoona7
 */
interface OnScrollChangedListener {

    fun onScrollChanged(wheelView: WheelView, scrollOffsetY: Int)

    fun onScrollStateChanged(wheelView: WheelView, state: Int)
}