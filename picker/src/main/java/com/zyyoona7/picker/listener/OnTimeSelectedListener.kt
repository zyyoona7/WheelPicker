package com.zyyoona7.picker.listener

/**
 * 时间选择回调
 *
 * @author zyyoona7
 */
interface OnTimeSelectedListener {

    fun onTimeSelected(is24Hour:Boolean,hour:Int,minute:Int,second:Int,isAm:Boolean)
}