package com.zyyoona7.wheel.listener

import com.zyyoona7.wheel.WheelViewKt

/**
 * WheelView 滚动变化监听器
 */
interface OnScrollChangedListener {

    fun onScrollChanged(wheelView: WheelViewKt, scrollOffsetY: Int)

    fun onScrollStateChanged(wheelView: WheelViewKt, @WheelViewKt.ScrollState state: Int)
}