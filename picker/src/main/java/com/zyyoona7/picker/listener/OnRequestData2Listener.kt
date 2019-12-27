package com.zyyoona7.picker.listener

import com.zyyoona7.wheel.WheelView

/**
 * 请求联动2 WheelView 数据
 *
 * @author zyyoona7
 */
interface OnRequestData2Listener {

    fun convert(linkage1Wv: WheelView):List<Any>
}