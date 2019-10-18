package com.zyyoona7.wheel.listener

import com.zyyoona7.wheel.WheelView

/**
 * WheelView 滚动变化监听器
 */
interface OnScrollChangedListener {

    fun onScrollChanged(wheelView: WheelView, scrollOffsetY: Int)

    fun onScrollStateChanged(wheelView: WheelView, @WheelView.ScrollState state: Int)
}