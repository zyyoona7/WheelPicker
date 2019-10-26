package com.zyyoona7.picker.interfaces

import com.zyyoona7.picker.listener.OnLinkageSelectedListener
import com.zyyoona7.picker.listener.OnRequestData2Listener
import com.zyyoona7.picker.listener.OnRequestData3Listener
import com.zyyoona7.wheel.WheelView
import com.zyyoona7.wheel.formatter.TextFormatter
import com.zyyoona7.wheel.listener.OnScrollChangedListener

interface LinkagePicker {

    fun setTextFormatter(textFormatter: TextFormatter)

    fun setFirstTextFormatter(textFormatter: TextFormatter)

    fun setSecondTextFormatter(textFormatter: TextFormatter)

    fun setThirdTextFormatter(textFormatter: TextFormatter)

    fun setOnRequestData2Listener(listener:OnRequestData2Listener?)

    fun setOnRequestData3Listener(listener: OnRequestData3Listener?)

    fun setData(firstData:List<Any>,useSecond:Boolean)

    fun setData(firstData: List<Any>,useSecond: Boolean,useThird:Boolean)

    fun setOnScrollChangedListener(listener:OnScrollChangedListener?)

    fun setOnLinkageSelectedListener(listener:OnLinkageSelectedListener?)

    fun setLeftText(linkage1Text:CharSequence,linkage2Text: CharSequence,linkage3Text: CharSequence)

    fun setRightText(linkage1Text:CharSequence,linkage2Text: CharSequence,linkage3Text: CharSequence)

    fun getFirstWheelView():WheelView

    fun getSecondWheelView():WheelView

    fun getThirdWheelView():WheelView
}