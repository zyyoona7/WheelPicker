package com.zyyoona7.picker.listener

import java.util.*

/**
 * 日期选择回调
 *
 * @author zyyoona7
 */
interface OnDateSelectedListener {

    fun onDateSelected(year: Int, month: Int, day: Int, date: Date)
}