package com.zyyoona7.picker.listener

import com.zyyoona7.wheel.WheelView

interface OnRequestData2Listener {

    fun convert(firstWv: WheelView):List<Any>
}