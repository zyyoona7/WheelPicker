package com.zyyoona7.picker.listener

import com.zyyoona7.wheel.WheelViewKt

interface OnRequestDataListener {

    fun convert(wheelView1:WheelViewKt, firstPosition:Int):List<Any>

    fun convert(wheelView1:WheelViewKt, firstPosition: Int, wheelView2:WheelViewKt, secondPosition:Int):List<Any>
}