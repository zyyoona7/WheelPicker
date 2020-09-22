package com.zyyoona7.picker.helper

import android.graphics.Paint
import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RawRes
import com.zyyoona7.picker.ex.*
import com.zyyoona7.picker.listener.*
import com.zyyoona7.wheel.WheelView
import com.zyyoona7.wheel.formatter.IntTextFormatter
import com.zyyoona7.wheel.formatter.TextFormatter
import com.zyyoona7.wheel.listener.OnScrollChangedListener
import java.util.*

/**
 * 设置 WheelView 相关的属性接口
 *
 * @author zyyoona7
 */
interface WheelPicker {

    fun setVisibleItems(visibleItems: Int)

    fun setLineSpacing(lineSpacingPx: Int)

    fun setLineSpacing(lineSpacingDp: Float)

    fun setCyclic(isCyclic: Boolean)

    /*
      ---------- WheelView 文字相关设置 ----------
     */

    fun setTextSize(textSizePx: Int)

    fun setTextSize(textSizeSp: Float)

    fun setAutoFitTextSize(autoFit: Boolean)

    fun setMinTextSize(minTextSizePx: Int)

    fun setMinTextSize(minTextSizeSp: Float)

    fun setTextAlign(textAlign: Paint.Align)

    fun setNormalTextColor(@ColorInt textColor: Int)

    fun setNormalTextColorRes(@ColorRes textColorRes: Int)

    fun setSelectedTextColor(@ColorInt textColor: Int)

    fun setSelectedTextColorRes(@ColorRes textColorRes: Int)

    fun setTextPadding(paddingPx: Int)

    fun setTextPadding(paddingDp: Float)

    fun setTextPaddingLeft(textPaddingLeftPx: Int)

    fun setTextPaddingLeft(textPaddingLeftDp: Float)

    fun setTextPaddingRight(textPaddingRightPx: Int)

    fun setTextPaddingRight(textPaddingRightDp: Float)

    fun setTypeface(typeface: Typeface)

    fun setTypeface(typeface: Typeface, isBoldForSelectedItem: Boolean)

    /*
      ---------- WheelView 文字相关设置 ----------
     */

    /*
      ---------- WheelView 分割线相关设置 ----------
     */
    fun setShowDivider(showDivider: Boolean)

    fun setDividerColor(@ColorInt dividerColor: Int)

    fun setDividerColorRes(@ColorRes dividerColorRes: Int)

    fun setDividerHeight(dividerHeightPx: Int)

    fun setDividerHeight(dividerHeightDp: Float)

    fun setDividerType(dividerType: WheelView.DividerType)

    fun setWheelDividerPadding(paddingPx: Int)

    fun setWheelDividerPadding(paddingDp: Float)

    fun setDividerCap(cap: Paint.Cap)

    fun setDividerOffsetY(offsetYPx: Int)

    fun setDividerOffsetY(offsetYDp: Float)
    /*
      ---------- WheelView 分割线相关设置 ----------
     */
    /*
      ---------- WheelView 选中区域蒙层相关设置 ----------
     */
    fun setShowCurtain(showCurtain: Boolean)

    fun setCurtainColor(@ColorInt curtainColor: Int)

    fun setCurtainColorRes(@ColorRes curtainColorRes: Int)
    /*
      ---------- WheelView 选中区域蒙层相关设置 ----------
     */
    /*
      ---------- WheelView 3D 效果相关设置 ----------
     */
    fun setCurved(curved: Boolean)

    fun setCurvedArcDirection(direction: WheelView.CurvedArcDirection)

    fun setCurvedArcDirectionFactor(factor: Float)

    fun setRefractRatio(ratio: Float)
    /*
      ---------- WheelView 3D 效果相关设置 ----------
     */

    fun setSoundEffect(soundEffect: Boolean)

    fun setSoundResource(@RawRes soundRes: Int)

    fun setSoundVolume(playVolume: Float)

    fun setResetSelectedPosition(reset: Boolean)

    /*
      ---------- 左右额外的文字相关设置 ----------
     */
    fun setLeftText(text: CharSequence)

    fun setRightText(text: CharSequence)

    fun setLeftTextSize(textSizePx: Int)

    fun setLeftTextSize(textSizeSp: Float)

    fun setRightTextSize(textSizePx: Int)

    fun setRightTextSize(textSizeSp: Float)

    fun setLeftTextColor(@ColorInt color: Int)

    fun setLeftTextColorRes(@ColorRes colorRes: Int)

    fun setRightTextColor(@ColorInt color: Int)

    fun setRightTextColorRes(@ColorRes colorRes: Int)

    fun setLeftTextMarginRight(marginRightPx: Int)

    fun setLeftTextMarginRight(marginRightDp: Float)

    fun setRightTextMarginLeft(marginLeftPx: Int)

    fun setRightTextMarginLeft(marginLeftDp: Float)

    fun setLeftTextGravity(gravity: Int)

    fun setRightTextGravity(gravity: Int)
    /*
      ---------- 左右额外的文字相关设置 ----------
     */
}

/**
 * 日期选择类需要实现的接口
 *
 * @author zyyoona7
 */
interface DatePicker {

    fun setYearTextFormatter(textFormatter: IntTextFormatter)

    fun setMonthTextFormatter(textFormatter: IntTextFormatter)

    fun setDayTextFormatter(textFormatter: IntTextFormatter)

    fun setOnDateSelectedListener(listener: OnDateSelectedListener?)

    fun setOnScrollChangedListener(listener: OnScrollChangedListener?)

    fun setYearRange(startYear: Int, endYear: Int)

    fun setSelectedDate(date: Date)

    fun setSelectedDate(calendar: Calendar)

    fun setSelectedDate(year: Int, month: Int, day: Int)

    fun setMaxSelectedDate(maxDate: Date)

    fun setMaxSelectedDate(maxDate: Date,selectedRangeMode: WheelView.SelectedRangeMode)

    fun setMaxSelectedDate(maxCalendar: Calendar)

    fun setMaxSelectedDate(maxCalendar: Calendar,selectedRangeMode: WheelView.SelectedRangeMode)

    fun setDateRange(minDate: Date, maxDate: Date)

    fun setDateRange(minDate: Date, maxDate: Date, selectedRangeMode: WheelView.SelectedRangeMode)

    fun setDateRange(minCalendar: Calendar, maxCalendar: Calendar)

    fun setDateRange(minCalendar: Calendar, maxCalendar: Calendar,
                     selectedRangeMode: WheelView.SelectedRangeMode)

    fun setLeftText(yearLeft: CharSequence, monthLeft: CharSequence, dayLeft: CharSequence)

    fun setRightText(yearRight: CharSequence, monthRight: CharSequence, dayRight: CharSequence)

    fun setShowYear(isShow: Boolean)

    fun setShowMonth(isShow: Boolean)

    fun setShowDay(isShow: Boolean)

    fun setYearMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setMonthMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setDayMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setMaxTextWidthMeasureType(yearType: WheelView.MeasureType,
                                   monthType: WheelView.MeasureType,
                                   dayType: WheelView.MeasureType)

    fun getSelectedDate(): Date

    /**
     * 选中日期字符串类型 yyyy-M-d
     */
    fun getSelectedDateStr(): String

    fun getSelectedYear(): Int

    fun getSelectedMonth(): Int

    fun getSelectedDay(): Int

    fun getWheelYearView(): WheelYearView

    fun getWheelMonthView(): WheelMonthView

    fun getWheelDayView(): WheelDayView
}

/**
 * 时间选择器需要实现类
 *
 * @author zyyoona7
 */
interface TimePicker {

    fun setAmPmTextHandler(textHandler: AmPmTextHandler)

    fun setHourTextFormatter(textFormatter: IntTextFormatter)

    fun setMinuteTextFormatter(textFormatter: IntTextFormatter)

    fun setSecondTextFormatter(textFormatter: IntTextFormatter)

    fun setOnScrollChangedListener(listener: OnScrollChangedListener?)

    fun setOnTimeSelectedListener(listener: OnTimeSelectedListener?)

    fun setLeftText(amPmText: CharSequence, hourText: CharSequence,
                    minuteText: CharSequence, secondText: CharSequence)

    fun setRightText(amPmText: CharSequence, hourText: CharSequence,
                     minuteText: CharSequence, secondText: CharSequence)

    fun set24Hour(is24Hour: Boolean)

    /**
     * 设置选中时间
     */
    fun setTime(calendar: Calendar, is24Hour: Boolean)

    /**
     * 设置选中时间
     */
    fun setTime(hour: Int, minute: Int, second: Int, is24Hour: Boolean, isAm: Boolean)

    /**
     * 设置选中时间 24小时制
     */
    fun setTimeFor24(hour: Int, minute: Int, second: Int)

    /**
     * 设置选中时间 12小时制
     */
    fun setTimeFor12(hour: Int, minute: Int, second: Int, isAm: Boolean)

    fun setShowHour(isShow: Boolean)

    fun setShowMinute(isShow: Boolean)

    fun setShowSecond(isShow: Boolean)

    fun setAmPmMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setHourMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setMinuteMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setSecondMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setMaxTextWidthMeasureType(amPmType: WheelView.MeasureType,
                                   hourType: WheelView.MeasureType,
                                   minuteType: WheelView.MeasureType,
                                   secondType: WheelView.MeasureType)

    fun is24Hour(): Boolean

    fun isAm():Boolean

    fun getSelectedHour(): Int

    fun getSelectedMinute(): Int

    fun getSelectedSecond(): Int

    fun getWheelAmPmView(): WheelAmPmView

    fun getWheelHourView(): WheelHourView

    fun getWheelMinuteView(): WheelMinuteView

    fun getWheelSecondView(): WheelSecondView
}


/**
 * 联动选择器需要实现此接口
 *
 * @author zyyoona7
 */
interface LinkagePicker {

    fun setTextFormatter(textFormatter: TextFormatter)

    fun setLinkage1TextFormatter(textFormatter: TextFormatter)

    fun setLinkage2TextFormatter(textFormatter: TextFormatter)

    fun setLinkage3TextFormatter(textFormatter: TextFormatter)

    fun setData(firstData: List<Any>, doubleLoadDataListener: OnDoubleLoadDataListener)

    fun setData(firstData: List<Any>, tripleLoadDataListener: OnTripleLoadDataListener)

    fun setSelectedPosition(linkage1Pos: Int, linkage2Pos: Int)

    fun setSelectedPosition(linkage1Pos: Int, linkage2Pos: Int, linkage3Pos: Int)

    fun setSelectedItem(linkage1Item: Any, linkage2Item: Any, isCompareFormatText: Boolean)

    fun setSelectedItem(linkage1Item: Any, linkage2Item: Any)

    fun setSelectedItem(linkage1Item: Any, linkage2Item: Any, linkage3Item: Any)

    fun setSelectedItem(linkage1Item: Any, linkage2Item: Any, linkage3Item: Any, isCompareFormatText: Boolean)

    fun setMaxTextWidthMeasureType(measureType: WheelView.MeasureType)

    fun setMaxTextWidthMeasureType(linkage1Type: WheelView.MeasureType,
                                   linkage2Type: WheelView.MeasureType,
                                   linkage3Type: WheelView.MeasureType)

    fun setOnScrollChangedListener(listener: OnScrollChangedListener?)

    fun setOnLinkageSelectedListener(listener: OnLinkageSelectedListener?)

    fun setLeftText(linkage1Text: CharSequence, linkage2Text: CharSequence, linkage3Text: CharSequence)

    fun setRightText(linkage1Text: CharSequence, linkage2Text: CharSequence, linkage3Text: CharSequence)

    fun <T> getLinkage1SelectedItem(): T?

    fun <T> getLinkage2SelectedItem(): T?

    fun <T> getLinkage3SelectedItem(): T?

    fun getLinkage1WheelView(): WheelView

    fun getLinkage2WheelView(): WheelView

    fun getLinkage3WheelView(): WheelView
}