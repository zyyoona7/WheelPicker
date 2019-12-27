package com.zyyoona7.picker.listener

import com.zyyoona7.wheel.WheelView

/**
 * 联动选择器选中回调
 *
 * @author zyyoona7
 */
interface OnLinkageSelectedListener {

    fun onLinkageSelected(firstWv:WheelView, secondWv:WheelView?, thirdWv:WheelView?)
}