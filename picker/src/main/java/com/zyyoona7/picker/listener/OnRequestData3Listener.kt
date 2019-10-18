package com.zyyoona7.picker.listener

import com.zyyoona7.wheel.WheelView

interface OnRequestData3Listener {

    fun convert(firstWv:WheelView, secondWv:WheelView):List<Any>
}