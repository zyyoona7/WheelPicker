package com.zyyoona7.picker.interfaces

import com.zyyoona7.wheel.formatter.TextFormatter

interface LinkagePicker {

    fun setFirstData(firstData:List<Any>)

    fun setFirstTextFormatter(textFormatter: TextFormatter)

    fun setSecondTextFormatter(textFormatter: TextFormatter)

    fun setThirdTextFormatter(textFormatter: TextFormatter)
}