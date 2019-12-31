package com.zyyoona7.picker.interfaces

/**
 * 将 Int 类型的值转换成 WheelView 下标
 */
interface IndexConverter {

    fun indexFor(item:Int):Int
}