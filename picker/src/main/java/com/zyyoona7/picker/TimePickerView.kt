package com.zyyoona7.picker

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RawRes
import com.zyyoona7.picker.ex.WheelAmPmView
import com.zyyoona7.picker.ex.WheelHourView
import com.zyyoona7.picker.ex.WheelMinuteView
import com.zyyoona7.picker.ex.WheelSecondView
import com.zyyoona7.picker.helper.TimePickerHelper
import com.zyyoona7.picker.interfaces.AmPmTextHandler
import com.zyyoona7.picker.interfaces.TimePicker
import com.zyyoona7.picker.interfaces.WheelPicker
import com.zyyoona7.wheel.WheelView
import com.zyyoona7.wheel.formatter.IntTextFormatter
import com.zyyoona7.wheel.listener.OnScrollChangedListener

/**
 * 时间选择器 View
 *
 * @author zyyoona7
 */
class TimePickerView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr), TimePicker, WheelPicker {

    private val timePickerHelper: TimePickerHelper
    private var widthWeightMode = false
    private var amPmWeight: Float = 1f
    private var hourWeight: Float = 1f
    private var minuteWeight: Float = 1f
    private var secondWeight: Float = 1f

    init {
        val amPmWheel = WheelAmPmView(context)
        val hourWheel = WheelHourView(context)
        val minuteWheel = WheelMinuteView(context)
        val secondWheel = WheelSecondView(context)
        timePickerHelper = TimePickerHelper(amPmWheel, hourWheel, minuteWheel, secondWheel)

        attrs?.let {
            initAttrs(context, it)
        }
        addViews(amPmWheel, hourWheel, minuteWheel, secondWheel)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimePickerView)
        widthWeightMode = typedArray.getBoolean(R.styleable.TimePickerView_tpv_widthWeightMode, false)
        amPmWeight = typedArray.getFloat(R.styleable.TimePickerView_tpv_amPmWeight, 1f)
        hourWeight = typedArray.getFloat(R.styleable.TimePickerView_tpv_hourWeight, 1f)
        minuteWeight = typedArray.getFloat(R.styleable.TimePickerView_tpv_minuteWeight, 1f)
        secondWeight = typedArray.getFloat(R.styleable.TimePickerView_tpv_secondWeight, 1f)
        setVisibleItems(typedArray.getInt(R.styleable.TimePickerView_tpv_visibleItems,
                WheelView.DEFAULT_VISIBLE_ITEM))
        setLineSpacing(typedArray.getDimensionPixelSize(R.styleable.TimePickerView_tpv_lineSpacing,
                WheelView.DEFAULT_LINE_SPACING))
        setCyclic(typedArray.getBoolean(R.styleable.TimePickerView_tpv_cyclic, false))
        setTextSize(typedArray.getDimensionPixelSize(R.styleable.TimePickerView_tpv_textSize,
                WheelView.DEFAULT_TEXT_SIZE))
        setTextAlign(typedArray.getInt(R.styleable.TimePickerView_tpv_textAlign,
                WheelView.TEXT_ALIGN_CENTER))
        setTextPadding(typedArray.getDimensionPixelSize(R.styleable.TimePickerView_tpv_textPadding,
                WheelView.DEFAULT_TEXT_PADDING))
        val amPmLeftText = typedArray.getText(R.styleable.TimePickerView_tpv_amPmLeftText)
                ?: ""
        val hourLeftText = typedArray.getText(R.styleable.TimePickerView_tpv_hourLeftText)
                ?: ""
        val minuteLeftText = typedArray.getText(R.styleable.TimePickerView_tpv_minuteLeftText)
                ?: ""
        val secondLeftText = typedArray.getText(R.styleable.TimePickerView_tpv_secondLeftText)
                ?: ""
        setLeftText(amPmLeftText, hourLeftText, minuteLeftText, secondLeftText)
        val amPmRightText = typedArray.getText(R.styleable.TimePickerView_tpv_amPmRightText)
                ?: ""
        val hourRightText = typedArray.getText(R.styleable.TimePickerView_tpv_hourRightText)
                ?: ""
        val minuteRightText = typedArray.getText(R.styleable.TimePickerView_tpv_minuteRightText)
                ?: ""
        val secondRightText = typedArray.getText(R.styleable.TimePickerView_tpv_secondRightText)
                ?: ""
        setRightText(amPmRightText, hourRightText, minuteRightText, secondRightText)
        setLeftTextSize(typedArray.getDimensionPixelSize(R.styleable.TimePickerView_tpv_leftTextSize,
                WheelView.DEFAULT_TEXT_SIZE))
        setRightTextSize(typedArray.getDimensionPixelSize(R.styleable.TimePickerView_tpv_rightTextSize,
                WheelView.DEFAULT_TEXT_SIZE))
        setLeftTextMarginRight(typedArray.getDimensionPixelSize(R.styleable.TimePickerView_tpv_leftTextMarginRight,
                WheelView.DEFAULT_TEXT_PADDING))
        setRightTextMarginLeft(typedArray.getDimensionPixelSize(R.styleable.TimePickerView_tpv_rightTextMarginLeft,
                WheelView.DEFAULT_TEXT_PADDING))
        setLeftTextColor(typedArray.getColor(R.styleable.TimePickerView_tpv_leftTextColor,
                WheelView.DEFAULT_SELECTED_TEXT_COLOR))
        setRightTextColor(typedArray.getColor(R.styleable.TimePickerView_tpv_rightTextColor,
                WheelView.DEFAULT_SELECTED_TEXT_COLOR))
        val leftGravity = typedArray.getInt(R.styleable.TimePickerView_tpv_leftTextGravity, 0)
        setLeftTextGravity(WheelView.getExtraGravity(leftGravity))
        val rightGravity = typedArray.getInt(R.styleable.TimePickerView_tpv_rightTextGravity, 0)
        setRightTextGravity(WheelView.getExtraGravity(rightGravity))
        setNormalTextColor(typedArray.getColor(R.styleable.TimePickerView_tpv_normalTextColor,
                WheelView.DEFAULT_NORMAL_TEXT_COLOR))
        setSelectedTextColor(typedArray.getColor(R.styleable.TimePickerView_tpv_selectedTextColor,
                WheelView.DEFAULT_SELECTED_TEXT_COLOR))
        setShowDivider(typedArray.getBoolean(R.styleable.TimePickerView_tpv_showDivider, false))
        setDividerType(typedArray.getInt(R.styleable.TimePickerView_tpv_dividerType, WheelView.DIVIDER_FILL))
        setDividerColor(typedArray.getColor(R.styleable.TimePickerView_tpv_dividerColor,
                WheelView.DEFAULT_SELECTED_TEXT_COLOR))
        setDividerHeight(typedArray.getDimensionPixelSize(R.styleable.TimePickerView_tpv_dividerHeight,
                WheelView.DEFAULT_DIVIDER_HEIGHT))
        setWheelDividerPadding(typedArray.getDimensionPixelSize(R.styleable.TimePickerView_tpv_dividerPadding,
                WheelView.DEFAULT_TEXT_PADDING))
        setDividerOffsetY(typedArray.getDimensionPixelOffset(R.styleable.TimePickerView_tpv_dividerOffsetY, 0))
        setCurved(typedArray.getBoolean(R.styleable.TimePickerView_tpv_curved, true))
        setCurvedArcDirection(typedArray.getInt(R.styleable.TimePickerView_tpv_curvedArcDirection,
                WheelView.CURVED_ARC_DIRECTION_CENTER))
        setCurvedArcDirectionFactor(typedArray.getFloat(R.styleable.TimePickerView_tpv_curvedArcDirectionFactor,
                WheelView.DEFAULT_CURVED_FACTOR))
        setShowCurtain(typedArray.getBoolean(R.styleable.TimePickerView_tpv_showCurtain, false))
        setCurtainColor(typedArray.getColor(R.styleable.TimePickerView_tpv_curtainColor, Color.TRANSPARENT))
        typedArray.recycle()
    }

    private fun addViews(amPmView: WheelAmPmView, hourView: WheelHourView,
                         minuteView: WheelMinuteView, secondView: WheelSecondView) {
        orientation = HORIZONTAL
        val width = if (widthWeightMode) 0 else ViewGroup.LayoutParams.WRAP_CONTENT
        val amPmLp = LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        val hourLp = LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        val minuteLp = LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        val secondLp = LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        amPmLp.gravity = Gravity.CENTER_VERTICAL
        hourLp.gravity = Gravity.CENTER_VERTICAL
        minuteLp.gravity = Gravity.CENTER_VERTICAL
        secondLp.gravity = Gravity.CENTER_VERTICAL
        if (widthWeightMode) {
            amPmLp.weight = amPmWeight
            hourLp.weight = hourWeight
            minuteLp.weight = minuteWeight
            secondLp.weight = secondWeight
        }
        addView(amPmView, amPmLp)
        addView(hourView, hourLp)
        addView(minuteView, minuteLp)
        addView(secondView, secondLp)
    }

    override fun setAmPmTextHandler(textHandler: AmPmTextHandler) {
        timePickerHelper.setAmPmTextHandler(textHandler)
    }

    override fun setHourTextFormatter(textFormatter: IntTextFormatter) {
        timePickerHelper.setHourTextFormatter(textFormatter)
    }

    override fun setMinuteTextFormatter(textFormatter: IntTextFormatter) {
        timePickerHelper.setMinuteTextFormatter(textFormatter)
    }

    override fun setSecondTextFormatter(textFormatter: IntTextFormatter) {
        timePickerHelper.setSecondTextFormatter(textFormatter)
    }

    override fun setOnScrollChangedListener(listener: OnScrollChangedListener?) {
        timePickerHelper.setOnScrollChangedListener(listener)
    }

    override fun setLeftText(amPmText: CharSequence, hourText: CharSequence,
                             minuteText: CharSequence, secondText: CharSequence) {
        timePickerHelper.setLeftText(amPmText, hourText, minuteText, secondText)
    }

    override fun setRightText(amPmText: CharSequence, hourText: CharSequence,
                              minuteText: CharSequence, secondText: CharSequence) {
        timePickerHelper.setRightText(amPmText, hourText, minuteText, secondText)
    }

    override fun getWheelAmPmView(): WheelAmPmView {
        return timePickerHelper.getWheelAmPmView()
    }

    override fun getWheelHourView(): WheelHourView {
        return timePickerHelper.getWheelHourView()
    }

    override fun getWheelMinuteView(): WheelMinuteView {
        return timePickerHelper.getWheelMinuteView()
    }

    override fun getWheelSecondView(): WheelSecondView {
        return timePickerHelper.getWheelSecondView()
    }

    override fun setVisibleItems(visibleItems: Int) {
        timePickerHelper.setVisibleItems(visibleItems)
    }

    override fun setLineSpacing(lineSpacingPx: Int) {
        timePickerHelper.setLineSpacing(lineSpacingPx)
    }

    override fun setLineSpacing(lineSpacingDp: Float) {
        timePickerHelper.setLineSpacing(lineSpacingDp)
    }

    override fun setCyclic(isCyclic: Boolean) {
        timePickerHelper.setCyclic(isCyclic)
    }

    override fun setTextSize(textSizePx: Int) {
        timePickerHelper.setTextSize(textSizePx)
    }

    override fun setTextSize(textSizeSp: Float) {
        timePickerHelper.setTextSize(textSizeSp)
    }

    override fun setAutoFitTextSize(autoFit: Boolean) {
        timePickerHelper.setAutoFitTextSize(autoFit)
    }

    override fun setMinTextSize(minTextSizePx: Int) {
        timePickerHelper.setMinTextSize(minTextSizePx)
    }

    override fun setMinTextSize(minTextSizeSp: Float) {
        timePickerHelper.setMinTextSize(minTextSizeSp)
    }

    override fun setTextAlign(@WheelView.TextAlign textAlign: Int) {
        timePickerHelper.setTextAlign(textAlign)
    }

    override fun setNormalTextColor(@ColorInt textColor: Int) {
        timePickerHelper.setNormalTextColor(textColor)
    }

    override fun setNormalTextColorRes(@ColorRes textColorRes: Int) {
        timePickerHelper.setNormalTextColorRes(textColorRes)
    }

    override fun setSelectedTextColor(@ColorInt textColor: Int) {
        timePickerHelper.setSelectedTextColor(textColor)
    }

    override fun setSelectedTextColorRes(@ColorRes textColorRes: Int) {
        timePickerHelper.setSelectedTextColorRes(textColorRes)
    }

    override fun setTextPadding(paddingPx: Int) {
        timePickerHelper.setTextPadding(paddingPx)
    }

    override fun setTextPadding(paddingDp: Float) {
        timePickerHelper.setTextPadding(paddingDp)
    }

    override fun setTextPaddingLeft(textPaddingLeftPx: Int) {
        timePickerHelper.setTextPaddingLeft(textPaddingLeftPx)
    }

    override fun setTextPaddingLeft(textPaddingLeftDp: Float) {
        timePickerHelper.setTextPaddingLeft(textPaddingLeftDp)
    }

    override fun setTextPaddingRight(textPaddingRightPx: Int) {
        timePickerHelper.setTextPaddingRight(textPaddingRightPx)
    }

    override fun setTextPaddingRight(textPaddingRightDp: Float) {
        timePickerHelper.setTextPaddingRight(textPaddingRightDp)
    }

    override fun setTypeface(typeface: Typeface) {
        timePickerHelper.setTypeface(typeface)
    }

    override fun setTypeface(typeface: Typeface, isBoldForSelectedItem: Boolean) {
        timePickerHelper.setTypeface(typeface, isBoldForSelectedItem)
    }

    override fun setShowDivider(showDivider: Boolean) {
        timePickerHelper.setShowDivider(showDivider)
    }

    override fun setDividerColor(@ColorInt dividerColor: Int) {
        timePickerHelper.setDividerColor(dividerColor)
    }

    override fun setDividerColorRes(@ColorRes dividerColorRes: Int) {
        timePickerHelper.setDividerColorRes(dividerColorRes)
    }

    override fun setDividerHeight(dividerHeightPx: Int) {
        timePickerHelper.setDividerHeight(dividerHeightPx)
    }

    override fun setDividerHeight(dividerHeightDp: Float) {
        timePickerHelper.setDividerHeight(dividerHeightDp)
    }

    override fun setDividerType(@WheelView.DividerType dividerType: Int) {
        timePickerHelper.setDividerType(dividerType)
    }

    override fun setWheelDividerPadding(paddingPx: Int) {
        timePickerHelper.setWheelDividerPadding(paddingPx)
    }

    override fun setWheelDividerPadding(paddingDp: Float) {
        timePickerHelper.setWheelDividerPadding(paddingDp)
    }

    override fun setDividerCap(cap: Paint.Cap) {
        timePickerHelper.setDividerCap(cap)
    }

    override fun setDividerOffsetY(offsetYPx: Int) {
        timePickerHelper.setDividerOffsetY(offsetYPx)
    }

    override fun setDividerOffsetY(offsetYDp: Float) {
        timePickerHelper.setDividerOffsetY(offsetYDp)
    }

    override fun setShowCurtain(showCurtain: Boolean) {
        timePickerHelper.setShowCurtain(showCurtain)
    }

    override fun setCurtainColor(@ColorInt curtainColor: Int) {
        timePickerHelper.setCurtainColor(curtainColor)
    }

    override fun setCurtainColorRes(@ColorRes curtainColorRes: Int) {
        timePickerHelper.setCurtainColorRes(curtainColorRes)
    }

    override fun setCurved(curved: Boolean) {
        timePickerHelper.setCurved(curved)
    }

    override fun setCurvedArcDirection(@WheelView.CurvedArcDirection direction: Int) {
        timePickerHelper.setCurvedArcDirection(direction)
    }

    override fun setCurvedArcDirectionFactor(factor: Float) {
        timePickerHelper.setCurvedArcDirectionFactor(factor)
    }

    override fun setRefractRatio(ratio: Float) {
        timePickerHelper.setRefractRatio(ratio)
    }

    override fun setSoundEffect(soundEffect: Boolean) {
        timePickerHelper.setSoundEffect(soundEffect)
    }

    override fun setSoundResource(@RawRes soundRes: Int) {
        timePickerHelper.setSoundResource(soundRes)
    }

    override fun setSoundVolume(playVolume: Float) {
        timePickerHelper.setSoundVolume(playVolume)
    }

    override fun setResetSelectedPosition(reset: Boolean) {
        timePickerHelper.setResetSelectedPosition(reset)
    }

    override fun setCanOverRangeScroll(canOverRange: Boolean) {
        timePickerHelper.setCanOverRangeScroll(canOverRange)
    }

    override fun setLeftText(text: CharSequence) {
        timePickerHelper.setLeftText(text)
    }

    override fun setRightText(text: CharSequence) {
        timePickerHelper.setRightText(text)
    }

    override fun setLeftTextSize(textSizePx: Int) {
        timePickerHelper.setLeftTextSize(textSizePx)
    }

    override fun setLeftTextSize(textSizeSp: Float) {
        timePickerHelper.setLeftTextSize(textSizeSp)
    }

    override fun setRightTextSize(textSizePx: Int) {
        timePickerHelper.setRightTextSize(textSizePx)
    }

    override fun setRightTextSize(textSizeSp: Float) {
        timePickerHelper.setRightTextSize(textSizeSp)
    }

    override fun setLeftTextColor(@ColorInt color: Int) {
        timePickerHelper.setLeftTextColor(color)
    }

    override fun setLeftTextColorRes(@ColorRes colorRes: Int) {
        timePickerHelper.setLeftTextColorRes(colorRes)
    }

    override fun setRightTextColor(@ColorInt color: Int) {
        timePickerHelper.setRightTextColor(color)
    }

    override fun setRightTextColorRes(@ColorRes colorRes: Int) {
        timePickerHelper.setRightTextColorRes(colorRes)
    }

    override fun setLeftTextMarginRight(marginRightPx: Int) {
        timePickerHelper.setLeftTextMarginRight(marginRightPx)
    }

    override fun setLeftTextMarginRight(marginRightDp: Float) {
        timePickerHelper.setLeftTextMarginRight(marginRightDp)
    }

    override fun setRightTextMarginLeft(marginLeftPx: Int) {
        timePickerHelper.setRightTextMarginLeft(marginLeftPx)
    }

    override fun setRightTextMarginLeft(marginLeftDp: Float) {
        timePickerHelper.setRightTextMarginLeft(marginLeftDp)
    }

    override fun setLeftTextGravity(gravity: Int) {
        timePickerHelper.setLeftTextGravity(gravity)
    }

    override fun setRightTextGravity(gravity: Int) {
        timePickerHelper.setRightTextGravity(gravity)
    }
}