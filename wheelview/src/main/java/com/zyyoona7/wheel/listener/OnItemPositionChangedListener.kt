package com.zyyoona7.wheel.listener

import com.zyyoona7.wheel.WheelViewKt

interface OnItemPositionChangedListener {

    fun onItemChanged(wheelView:WheelViewKt,oldPosition:Int,newPosition:Int)
}