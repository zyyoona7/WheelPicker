package com.zyyoona7.picker.interfaces

import com.zyyoona7.picker.listener.OnLinkageSelectedListener
import com.zyyoona7.picker.listener.OnRequestData2Listener
import com.zyyoona7.picker.listener.OnRequestData3Listener
import com.zyyoona7.wheel.WheelView
import com.zyyoona7.wheel.formatter.TextFormatter
import com.zyyoona7.wheel.listener.OnScrollChangedListener

interface LinkagePicker {

    fun setTextFormatter(textFormatter: TextFormatter)

    fun setLinkage1TextFormatter(textFormatter: TextFormatter)

    fun setLinkage2TextFormatter(textFormatter: TextFormatter)

    fun setLinkage3TextFormatter(textFormatter: TextFormatter)

    fun setOnRequestData2Listener(listener:OnRequestData2Listener?)

    fun setOnRequestData3Listener(listener: OnRequestData3Listener?)

    fun setData(firstData:List<Any>,useSecond:Boolean)

    fun setData(firstData: List<Any>,useSecond: Boolean,useThird:Boolean)

    fun setMaxTextWidthMeasureType(@WheelView.MeasureType measureType:Int)

    fun setMaxTextWidthMeasureType(@WheelView.MeasureType linkage1Type:Int,
                              @WheelView.MeasureType linkage2Type:Int,
                              @WheelView.MeasureType linkage3Type:Int)

    fun setOnScrollChangedListener(listener:OnScrollChangedListener?)

    fun setOnLinkageSelectedListener(listener:OnLinkageSelectedListener?)

    fun setLeftText(linkage1Text:CharSequence,linkage2Text: CharSequence,linkage3Text: CharSequence)

    fun setRightText(linkage1Text:CharSequence,linkage2Text: CharSequence,linkage3Text: CharSequence)

    fun getLinkage1WheelView():WheelView

    fun getLinkage2WheelView():WheelView

    fun getLinkage3WheelView():WheelView

    //todo List<T>  List<List<T>> 这种结构的兼容
    //todo 设置选中下标，设置选中的文本
}