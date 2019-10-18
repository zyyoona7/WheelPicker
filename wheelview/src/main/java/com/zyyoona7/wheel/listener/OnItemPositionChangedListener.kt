package com.zyyoona7.wheel.listener

import com.zyyoona7.wheel.WheelView

/**
 * WheelView 滚动时 position 变化监听器
 */
interface OnItemPositionChangedListener {

    fun onItemChanged(wheelView:WheelView,oldPosition:Int,newPosition:Int)
}