package com.zyyoona7.picker.helper

import android.graphics.Paint
import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RawRes
import com.zyyoona7.picker.ex.WheelAmPmView
import com.zyyoona7.picker.ex.WheelHourView
import com.zyyoona7.picker.ex.WheelMinuteView
import com.zyyoona7.picker.ex.WheelSecondView
import com.zyyoona7.picker.interfaces.AmPmTextHandler
import com.zyyoona7.picker.interfaces.TimePicker
import com.zyyoona7.picker.interfaces.WheelPicker
import com.zyyoona7.wheel.WheelView
import com.zyyoona7.wheel.adapter.ArrayWheelAdapter
import com.zyyoona7.wheel.formatter.IntTextFormatter
import com.zyyoona7.wheel.listener.OnItemSelectedListener
import com.zyyoona7.wheel.listener.OnScrollChangedListener
import kotlin.math.min

class TimePickerHelper(private var amPmWheel: WheelAmPmView?,
                       private var hourWheel: WheelHourView?,
                       private var minuteWheel: WheelMinuteView?,
                       private var secondWheel: WheelSecondView?)
    : OnItemSelectedListener, OnScrollChangedListener, TimePicker, WheelPicker {

    private var scrollChangedListener: OnScrollChangedListener? = null

    init {
        amPmWheel?.setOnItemSelectedListener(this)
        hourWheel?.setOnItemSelectedListener(this)
        minuteWheel?.setOnItemSelectedListener(this)
        secondWheel?.setOnItemSelectedListener(this)

        amPmWheel?.setOnScrollChangedListener(this)
        hourWheel?.setOnScrollChangedListener(this)
        minuteWheel?.setOnScrollChangedListener(this)
        secondWheel?.setOnScrollChangedListener(this)
    }

    override fun onItemSelected(wheelView: WheelView, adapter: ArrayWheelAdapter<*>, position: Int) {
    }

    override fun onScrollChanged(wheelView: WheelView, scrollOffsetY: Int) {
    }

    override fun onScrollStateChanged(wheelView: WheelView, state: Int) {
    }

    override fun setAmPmTextHandler(textHandler: AmPmTextHandler) {
        amPmWheel?.amPmTextHandler=textHandler
    }

    override fun setHourTextFormatter(textFormatter: IntTextFormatter) {
        hourWheel?.setTextFormatter(textFormatter)
    }

    override fun setMinuteTextFormatter(textFormatter: IntTextFormatter) {
        minuteWheel?.setTextFormatter(textFormatter)
    }

    override fun setSecondTextFormatter(textFormatter: IntTextFormatter) {
        secondWheel?.setTextFormatter(textFormatter)
    }

    override fun setOnScrollChangedListener(listener: OnScrollChangedListener?) {
        scrollChangedListener=listener
    }

    override fun getWheelAmPmView(): WheelAmPmView {
        require(amPmWheel != null) {
            "WheelAmPmView is null."
        }
        return amPmWheel!!
    }

    override fun getWheelHourView(): WheelHourView {
        require(hourWheel != null) {
            "WheelHourView is null."
        }
        return hourWheel!!
    }

    override fun getWheelMinuteView(): WheelMinuteView {
        require(minuteWheel != null) {
            "WheelMinuteView is null."
        }
        return minuteWheel!!
    }

    override fun getWheelSecondView(): WheelSecondView {
        require(secondWheel != null) {
            "WheelSecondView is null."
        }
        return secondWheel!!
    }

    override fun setVisibleItems(visibleItems: Int) {
        amPmWheel?.visibleItems = visibleItems
        hourWheel?.visibleItems = visibleItems
        minuteWheel?.visibleItems = visibleItems
        secondWheel?.visibleItems = visibleItems
    }

    override fun setLineSpacing(lineSpacingPx: Int) {
        amPmWheel?.lineSpacing = lineSpacingPx
        hourWheel?.lineSpacing = lineSpacingPx
        minuteWheel?.lineSpacing = lineSpacingPx
        secondWheel?.lineSpacing = lineSpacingPx
    }

    override fun setLineSpacing(lineSpacingDp: Float) {
        amPmWheel?.setLineSpacing(lineSpacingDp)
        hourWheel?.setLineSpacing(lineSpacingDp)
        minuteWheel?.setLineSpacing(lineSpacingDp)
        secondWheel?.setLineSpacing(lineSpacingDp)
    }

    override fun setCyclic(isCyclic: Boolean) {
        amPmWheel?.isCyclic = isCyclic
        hourWheel?.isCyclic = isCyclic
        minuteWheel?.isCyclic = isCyclic
        secondWheel?.isCyclic = isCyclic
    }

    override fun setTextSize(textSizePx: Int) {
        amPmWheel?.textSize = textSizePx
        hourWheel?.textSize = textSizePx
        minuteWheel?.textSize = textSizePx
        secondWheel?.textSize = textSizePx
    }

    override fun setTextSize(textSizeSp: Float) {
        amPmWheel?.setTextSize(textSizeSp)
        hourWheel?.setTextSize(textSizeSp)
        minuteWheel?.setTextSize(textSizeSp)
        secondWheel?.setTextSize(textSizeSp)
    }

    override fun setAutoFitTextSize(autoFit: Boolean) {
        amPmWheel?.isAutoFitTextSize = autoFit
        hourWheel?.isAutoFitTextSize = autoFit
        minuteWheel?.isAutoFitTextSize = autoFit
        secondWheel?.isAutoFitTextSize = autoFit
    }

    override fun setMinTextSize(minTextSizePx: Int) {
        amPmWheel?.minTextSize = minTextSizePx
        hourWheel?.minTextSize = minTextSizePx
        minuteWheel?.minTextSize = minTextSizePx
        secondWheel?.minTextSize = minTextSizePx
    }

    override fun setMinTextSize(minTextSizeSp: Float) {
        amPmWheel?.setMinTextSize(minTextSizeSp)
        hourWheel?.setMinTextSize(minTextSizeSp)
        minuteWheel?.setMinTextSize(minTextSizeSp)
        secondWheel?.setMinTextSize(minTextSizeSp)
    }

    override fun setTextAlign(@WheelView.TextAlign textAlign: Int) {
        amPmWheel?.textAlign = textAlign
        hourWheel?.textAlign = textAlign
        minuteWheel?.textAlign = textAlign
        secondWheel?.textAlign = textAlign
    }

    override fun setNormalTextColor(@ColorInt textColor: Int) {
        amPmWheel?.normalTextColor = textColor
        hourWheel?.normalTextColor = textColor
        minuteWheel?.normalTextColor = textColor
        secondWheel?.normalTextColor = textColor
    }

    override fun setNormalTextColorRes(@ColorRes textColorRes: Int) {
        amPmWheel?.setNormalTextColorRes(textColorRes)
        hourWheel?.setNormalTextColorRes(textColorRes)
        minuteWheel?.setNormalTextColorRes(textColorRes)
        secondWheel?.setNormalTextColorRes(textColorRes)
    }

    override fun setSelectedTextColor(@ColorInt textColor: Int) {
        amPmWheel?.selectedTextColor = textColor
        hourWheel?.selectedTextColor = textColor
        minuteWheel?.selectedTextColor = textColor
        secondWheel?.selectedTextColor = textColor
    }

    override fun setSelectedTextColorRes(@ColorRes textColorRes: Int) {
        amPmWheel?.setSelectedTextColorRes(textColorRes)
        hourWheel?.setSelectedTextColorRes(textColorRes)
        minuteWheel?.setSelectedTextColorRes(textColorRes)
        secondWheel?.setSelectedTextColorRes(textColorRes)
    }

    override fun setTextPadding(paddingPx: Int) {
        amPmWheel?.textPaddingLeft = paddingPx
        amPmWheel?.textPaddingRight = paddingPx
        hourWheel?.textPaddingLeft = paddingPx
        hourWheel?.textPaddingRight = paddingPx
        minuteWheel?.textPaddingLeft = paddingPx
        minuteWheel?.textPaddingRight = paddingPx
        secondWheel?.textPaddingLeft = paddingPx
        secondWheel?.textPaddingRight = paddingPx
    }

    override fun setTextPadding(paddingDp: Float) {
        amPmWheel?.setTextPadding(paddingDp)
        hourWheel?.setTextPadding(paddingDp)
        minuteWheel?.setTextPadding(paddingDp)
        secondWheel?.setTextPadding(paddingDp)
    }

    override fun setTextPaddingLeft(textPaddingLeftPx: Int) {
        amPmWheel?.textPaddingLeft = textPaddingLeftPx
        hourWheel?.textPaddingLeft = textPaddingLeftPx
        minuteWheel?.textPaddingLeft = textPaddingLeftPx
        secondWheel?.textPaddingLeft = textPaddingLeftPx
    }

    override fun setTextPaddingLeft(textPaddingLeftDp: Float) {
        amPmWheel?.setTextPaddingLeft(textPaddingLeftDp)
        hourWheel?.setTextPaddingLeft(textPaddingLeftDp)
        minuteWheel?.setTextPaddingLeft(textPaddingLeftDp)
        secondWheel?.setTextPaddingLeft(textPaddingLeftDp)
    }

    override fun setTextPaddingRight(textPaddingRightPx: Int) {
        amPmWheel?.textPaddingRight = textPaddingRightPx
        hourWheel?.textPaddingRight = textPaddingRightPx
        minuteWheel?.textPaddingRight = textPaddingRightPx
        secondWheel?.textPaddingRight = textPaddingRightPx
    }

    override fun setTextPaddingRight(textPaddingRightDp: Float) {
        amPmWheel?.setTextPaddingRight(textPaddingRightDp)
        hourWheel?.setTextPaddingRight(textPaddingRightDp)
        minuteWheel?.setTextPaddingRight(textPaddingRightDp)
        secondWheel?.setTextPaddingRight(textPaddingRightDp)
    }

    override fun setTypeface(typeface: Typeface) {
        amPmWheel?.setTypeface(typeface, false)
        hourWheel?.setTypeface(typeface, false)
        minuteWheel?.setTypeface(typeface, false)
        secondWheel?.setTypeface(typeface, false)
    }

    override fun setTypeface(typeface: Typeface, isBoldForSelectedItem: Boolean) {
        amPmWheel?.setTypeface(typeface, isBoldForSelectedItem)
        hourWheel?.setTypeface(typeface, isBoldForSelectedItem)
        minuteWheel?.setTypeface(typeface, isBoldForSelectedItem)
        secondWheel?.setTypeface(typeface, isBoldForSelectedItem)
    }

    override fun setShowDivider(showDivider: Boolean) {
        amPmWheel?.isShowDivider = showDivider
        hourWheel?.isShowDivider = showDivider
        minuteWheel?.isShowDivider = showDivider
        secondWheel?.isShowDivider = showDivider
    }

    override fun setDividerColor(@ColorInt dividerColor: Int) {
        amPmWheel?.dividerColor = dividerColor
        hourWheel?.dividerColor = dividerColor
        minuteWheel?.dividerColor = dividerColor
        secondWheel?.dividerColor = dividerColor
    }

    override fun setDividerColorRes(@ColorRes dividerColorRes: Int) {
        amPmWheel?.setDividerColorRes(dividerColorRes)
        hourWheel?.setDividerColorRes(dividerColorRes)
        minuteWheel?.setDividerColorRes(dividerColorRes)
        secondWheel?.setDividerColorRes(dividerColorRes)
    }

    override fun setDividerHeight(dividerHeightPx: Int) {
        amPmWheel?.dividerHeight = dividerHeightPx
        hourWheel?.dividerHeight = dividerHeightPx
        minuteWheel?.dividerHeight = dividerHeightPx
        secondWheel?.dividerHeight = dividerHeightPx
    }

    override fun setDividerHeight(dividerHeightDp: Float) {
        amPmWheel?.setDividerHeight(dividerHeightDp)
        hourWheel?.setDividerHeight(dividerHeightDp)
        minuteWheel?.setDividerHeight(dividerHeightDp)
        secondWheel?.setDividerHeight(dividerHeightDp)
    }

    override fun setDividerType(@WheelView.DividerType dividerType: Int) {
        amPmWheel?.dividerType = dividerType
        hourWheel?.dividerType = dividerType
        minuteWheel?.dividerType = dividerType
        secondWheel?.dividerType = dividerType
    }

    override fun setWheelDividerPadding(paddingPx: Int) {
        amPmWheel?.dividerPadding = paddingPx
        hourWheel?.dividerPadding = paddingPx
        minuteWheel?.dividerPadding = paddingPx
        secondWheel?.dividerPadding = paddingPx
    }

    override fun setWheelDividerPadding(paddingDp: Float) {
        amPmWheel?.setDividerPadding(paddingDp)
        hourWheel?.setDividerPadding(paddingDp)
        minuteWheel?.setDividerPadding(paddingDp)
        secondWheel?.setDividerPadding(paddingDp)
    }

    override fun setDividerCap(cap: Paint.Cap) {
        amPmWheel?.dividerCap = cap
        hourWheel?.dividerCap = cap
        minuteWheel?.dividerCap = cap
        secondWheel?.dividerCap = cap
    }

    override fun setDividerOffsetY(offsetYPx: Int) {
        amPmWheel?.dividerOffsetY = offsetYPx
        hourWheel?.dividerOffsetY = offsetYPx
        minuteWheel?.dividerOffsetY = offsetYPx
        secondWheel?.dividerOffsetY = offsetYPx
    }

    override fun setDividerOffsetY(offsetYDp: Float) {
        amPmWheel?.setDividerOffsetY(offsetYDp)
        hourWheel?.setDividerOffsetY(offsetYDp)
        minuteWheel?.setDividerOffsetY(offsetYDp)
        secondWheel?.setDividerOffsetY(offsetYDp)
    }

    override fun setShowCurtain(showCurtain: Boolean) {
        amPmWheel?.isShowCurtain = showCurtain
        hourWheel?.isShowCurtain = showCurtain
        minuteWheel?.isShowCurtain = showCurtain
        secondWheel?.isShowCurtain = showCurtain
    }

    override fun setCurtainColor(@ColorInt curtainColor: Int) {
        amPmWheel?.curtainColor = curtainColor
        hourWheel?.curtainColor = curtainColor
        minuteWheel?.curtainColor = curtainColor
        secondWheel?.curtainColor = curtainColor
    }

    override fun setCurtainColorRes(@ColorRes curtainColorRes: Int) {
        amPmWheel?.setCurtainColorRes(curtainColorRes)
        hourWheel?.setCurtainColorRes(curtainColorRes)
        minuteWheel?.setCurtainColorRes(curtainColorRes)
        secondWheel?.setCurtainColorRes(curtainColorRes)
    }

    override fun setCurved(curved: Boolean) {
        amPmWheel?.isCurved = curved
        hourWheel?.isCurved = curved
        minuteWheel?.isCurved = curved
        secondWheel?.isCurved = curved
    }

    override fun setCurvedArcDirection(@WheelView.CurvedArcDirection direction: Int) {
        amPmWheel?.curvedArcDirection = direction
        hourWheel?.curvedArcDirection = direction
        minuteWheel?.curvedArcDirection = direction
        secondWheel?.curvedArcDirection = direction
    }

    override fun setCurvedArcDirectionFactor(factor: Float) {
        amPmWheel?.curvedArcDirectionFactor = factor
        hourWheel?.curvedArcDirectionFactor = factor
        minuteWheel?.curvedArcDirectionFactor = factor
        secondWheel?.curvedArcDirectionFactor = factor
    }

    override fun setRefractRatio(ratio: Float) {
        amPmWheel?.refractRatio = ratio
        hourWheel?.refractRatio = ratio
        minuteWheel?.refractRatio = ratio
        secondWheel?.refractRatio = ratio
    }

    override fun setSoundEffect(soundEffect: Boolean) {
        amPmWheel?.isSoundEffect = soundEffect
        hourWheel?.isSoundEffect = soundEffect
        minuteWheel?.isSoundEffect = soundEffect
        secondWheel?.isSoundEffect = soundEffect
    }

    override fun setSoundResource(@RawRes soundRes: Int) {
        amPmWheel?.setSoundResource(soundRes)
        hourWheel?.setSoundResource(soundRes)
        minuteWheel?.setSoundResource(soundRes)
        secondWheel?.setSoundResource(soundRes)
    }

    override fun setSoundVolume(playVolume: Float) {
        amPmWheel?.setSoundVolume(playVolume)
        hourWheel?.setSoundVolume(playVolume)
        minuteWheel?.setSoundVolume(playVolume)
        secondWheel?.setSoundVolume(playVolume)
    }

    override fun setResetSelectedPosition(reset: Boolean) {
        amPmWheel?.isResetSelectedPosition = reset
        hourWheel?.isResetSelectedPosition = reset
        minuteWheel?.isResetSelectedPosition = reset
        secondWheel?.isResetSelectedPosition = reset
    }

    override fun setCanOverRangeScroll(canOverRange: Boolean) {
        amPmWheel?.canOverRangeScroll = canOverRange
        hourWheel?.canOverRangeScroll = canOverRange
        minuteWheel?.canOverRangeScroll = canOverRange
        secondWheel?.canOverRangeScroll = canOverRange
    }

    override fun setLeftText(text: CharSequence) {
        setLeftText(text, text, text, text)
    }

    override fun setLeftText(amPmText: CharSequence, hourText: CharSequence,
                             minuteText: CharSequence, secondText: CharSequence) {
        amPmWheel?.leftText = amPmText
        hourWheel?.leftText = hourText
        minuteWheel?.leftText = minuteText
        secondWheel?.leftText = secondText
    }

    override fun setRightText(text: CharSequence) {
        setRightText(text, text, text, text)
    }

    override fun setRightText(amPmText: CharSequence, hourText: CharSequence,
                              minuteText: CharSequence, secondText: CharSequence) {
        amPmWheel?.rightText = amPmText
        hourWheel?.rightText = hourText
        minuteWheel?.rightText = minuteText
        secondWheel?.rightText = secondText
    }

    override fun setLeftTextSize(textSizePx: Int) {
        amPmWheel?.leftTextSize = textSizePx
        hourWheel?.leftTextSize = textSizePx
        minuteWheel?.leftTextSize = textSizePx
        secondWheel?.leftTextSize = textSizePx
    }

    override fun setLeftTextSize(textSizeSp: Float) {
        amPmWheel?.setLeftTextSize(textSizeSp)
        hourWheel?.setLeftTextSize(textSizeSp)
        minuteWheel?.setLeftTextSize(textSizeSp)
        secondWheel?.setLeftTextSize(textSizeSp)
    }

    override fun setRightTextSize(textSizePx: Int) {
        amPmWheel?.rightTextSize = textSizePx
        hourWheel?.rightTextSize = textSizePx
        minuteWheel?.rightTextSize = textSizePx
        secondWheel?.rightTextSize = textSizePx
    }

    override fun setRightTextSize(textSizeSp: Float) {
        amPmWheel?.setRightTextSize(textSizeSp)
        hourWheel?.setRightTextSize(textSizeSp)
        minuteWheel?.setRightTextSize(textSizeSp)
        secondWheel?.setRightTextSize(textSizeSp)
    }

    override fun setLeftTextColor(@ColorInt color: Int) {
        amPmWheel?.leftTextColor = color
        hourWheel?.leftTextColor = color
        minuteWheel?.leftTextColor = color
        secondWheel?.leftTextColor = color
    }

    override fun setLeftTextColorRes(@ColorRes colorRes: Int) {
        amPmWheel?.setLeftTextColorRes(colorRes)
        hourWheel?.setLeftTextColorRes(colorRes)
        minuteWheel?.setLeftTextColorRes(colorRes)
        secondWheel?.setLeftTextColorRes(colorRes)
    }

    override fun setRightTextColor(@ColorInt color: Int) {
        amPmWheel?.rightTextColor = color
        hourWheel?.rightTextColor = color
        minuteWheel?.rightTextColor = color
        secondWheel?.rightTextColor = color
    }

    override fun setRightTextColorRes(@ColorRes colorRes: Int) {
        amPmWheel?.setRightTextColorRes(colorRes)
        hourWheel?.setRightTextColorRes(colorRes)
        minuteWheel?.setRightTextColorRes(colorRes)
        secondWheel?.setRightTextColorRes(colorRes)
    }

    override fun setLeftTextMarginRight(marginRightPx: Int) {
        amPmWheel?.leftTextMarginRight = marginRightPx
        hourWheel?.leftTextMarginRight = marginRightPx
        minuteWheel?.leftTextMarginRight = marginRightPx
        secondWheel?.leftTextMarginRight = marginRightPx
    }

    override fun setLeftTextMarginRight(marginRightDp: Float) {
        amPmWheel?.setLeftTextMarginRight(marginRightDp)
        hourWheel?.setLeftTextMarginRight(marginRightDp)
        minuteWheel?.setLeftTextMarginRight(marginRightDp)
        secondWheel?.setLeftTextMarginRight(marginRightDp)
    }

    override fun setRightTextMarginLeft(marginLeftPx: Int) {
        amPmWheel?.rightTextMarginLeft = marginLeftPx
        hourWheel?.rightTextMarginLeft = marginLeftPx
        minuteWheel?.rightTextMarginLeft = marginLeftPx
        secondWheel?.rightTextMarginLeft = marginLeftPx
    }

    override fun setRightTextMarginLeft(marginLeftDp: Float) {
        amPmWheel?.setRightTextMarginLeft(marginLeftDp)
        hourWheel?.setRightTextMarginLeft(marginLeftDp)
        minuteWheel?.setRightTextMarginLeft(marginLeftDp)
        secondWheel?.setRightTextMarginLeft(marginLeftDp)
    }

    override fun setLeftTextGravity(gravity: Int) {
        amPmWheel?.leftTextGravity = gravity
        hourWheel?.leftTextGravity = gravity
        minuteWheel?.leftTextGravity = gravity
        secondWheel?.leftTextGravity = gravity
    }

    override fun setRightTextGravity(gravity: Int) {
        amPmWheel?.rightTextGravity = gravity
        hourWheel?.rightTextGravity = gravity
        minuteWheel?.rightTextGravity = gravity
        secondWheel?.rightTextGravity = gravity
    }
}