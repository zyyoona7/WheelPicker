package com.zyyoona7.picker.listener

import com.zyyoona7.wheel.WheelView

/**
 * 请求联动3 WheelView 数据
 *
 * @author zyyoona7
 */
interface OnRequestData3Listener {

    fun convert(linkage1Wv:WheelView, linkage2Wv:WheelView):List<Any>
}