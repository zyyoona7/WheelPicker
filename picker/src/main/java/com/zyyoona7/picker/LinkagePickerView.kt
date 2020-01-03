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
import com.zyyoona7.picker.helper.LinkagePickerHelper
import com.zyyoona7.picker.helper.LinkagePicker
import com.zyyoona7.picker.helper.WheelPicker
import com.zyyoona7.picker.listener.OnLinkageSelectedListener
import com.zyyoona7.picker.listener.OnRequestData2Listener
import com.zyyoona7.picker.listener.OnRequestData3Listener
import com.zyyoona7.wheel.WheelView
import com.zyyoona7.wheel.formatter.TextFormatter
import com.zyyoona7.wheel.listener.OnScrollChangedListener

/**
 * 联动选择器 View
 *
 * @author zyyoona7
 */
class LinkagePickerView @JvmOverloads constructor(context: Context,
                                                  attrs: AttributeSet? = null,
                                                  defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr), LinkagePicker, WheelPicker {

    private val linkagePickerHelper: LinkagePickerHelper

    init {
        val wheelView1 = WheelView(context)
        val wheelView2 = WheelView(context)
        val wheelView3 = WheelView(context)
        wheelView1.id = R.id.wheel_linkage1
        wheelView2.id = R.id.wheel_linkage2
        wheelView3.id = R.id.wheel_linkage3
        linkagePickerHelper = LinkagePickerHelper(wheelView1, wheelView2, wheelView3)
        attrs?.let {
            initAttrs(context, it)
        }
        val layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.weight = 1f
        layoutParams.gravity = Gravity.CENTER_VERTICAL
        addView(wheelView1, layoutParams)
        addView(wheelView2, layoutParams)
        addView(wheelView3, layoutParams)
        setMaxTextWidthMeasureType(WheelView.MeasureType.MAX_LENGTH)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LinkagePickerView)
        setVisibleItems(typedArray.getInt(R.styleable.LinkagePickerView_lpv_visibleItems,
                WheelView.DEFAULT_VISIBLE_ITEM))
        setLineSpacing(typedArray.getDimensionPixelSize(R.styleable.LinkagePickerView_lpv_lineSpacing,
                WheelView.DEFAULT_LINE_SPACING))
        setCyclic(typedArray.getBoolean(R.styleable.LinkagePickerView_lpv_cyclic, false))
        setTextSize(typedArray.getDimensionPixelSize(R.styleable.LinkagePickerView_lpv_textSize,
                WheelView.DEFAULT_TEXT_SIZE))
        setTextAlign(WheelView.convertTextAlign(typedArray.getInt(R.styleable.LinkagePickerView_lpv_textAlign,
                WheelView.TEXT_ALIGN_CENTER)))
        setTextPadding(typedArray.getDimensionPixelSize(R.styleable.LinkagePickerView_lpv_textPadding,
                WheelView.DEFAULT_TEXT_PADDING))
        val linkage1LeftText = typedArray.getText(R.styleable.LinkagePickerView_lpv_linkage1LeftText)
                ?: ""
        val linkage2LeftText = typedArray.getText(R.styleable.LinkagePickerView_lpv_linkage2LeftText)
                ?: ""
        val linkage3LeftText = typedArray.getText(R.styleable.LinkagePickerView_lpv_linkage3LeftText)
                ?: ""
        setLeftText(linkage1LeftText, linkage2LeftText, linkage3LeftText)
        val linkage1RightText = typedArray.getText(R.styleable.LinkagePickerView_lpv_linkage1RightText)
                ?: ""
        val linkage2RightText = typedArray.getText(R.styleable.LinkagePickerView_lpv_linkage2RightText)
                ?: ""
        val linkage3RightText = typedArray.getText(R.styleable.LinkagePickerView_lpv_linkage3RightText)
                ?: ""
        setRightText(linkage1RightText, linkage2RightText, linkage3RightText)
        setLeftTextSize(typedArray.getDimensionPixelSize(R.styleable.LinkagePickerView_lpv_leftTextSize,
                WheelView.DEFAULT_TEXT_SIZE))
        setRightTextSize(typedArray.getDimensionPixelSize(R.styleable.LinkagePickerView_lpv_rightTextSize,
                WheelView.DEFAULT_TEXT_SIZE))
        setLeftTextMarginRight(typedArray.getDimensionPixelSize(R.styleable.LinkagePickerView_lpv_leftTextMarginRight,
                WheelView.DEFAULT_TEXT_PADDING))
        setRightTextMarginLeft(typedArray.getDimensionPixelSize(R.styleable.LinkagePickerView_lpv_rightTextMarginLeft,
                WheelView.DEFAULT_TEXT_PADDING))
        setLeftTextColor(typedArray.getColor(R.styleable.LinkagePickerView_lpv_leftTextColor,
                WheelView.DEFAULT_SELECTED_TEXT_COLOR))
        setRightTextColor(typedArray.getColor(R.styleable.LinkagePickerView_lpv_rightTextColor,
                WheelView.DEFAULT_SELECTED_TEXT_COLOR))
        val leftGravity = typedArray.getInt(R.styleable.LinkagePickerView_lpv_leftTextGravity, 0)
        setLeftTextGravity(WheelView.covertExtraGravity(leftGravity))
        val rightGravity = typedArray.getInt(R.styleable.LinkagePickerView_lpv_rightTextGravity, 0)
        setRightTextGravity(WheelView.covertExtraGravity(rightGravity))
        setNormalTextColor(typedArray.getColor(R.styleable.LinkagePickerView_lpv_normalTextColor,
                WheelView.DEFAULT_NORMAL_TEXT_COLOR))
        setSelectedTextColor(typedArray.getColor(R.styleable.LinkagePickerView_lpv_selectedTextColor,
                WheelView.DEFAULT_SELECTED_TEXT_COLOR))
        setShowDivider(typedArray.getBoolean(R.styleable.LinkagePickerView_lpv_showDivider, false))
        setDividerType(WheelView.convertDividerType(typedArray.getInt(R.styleable.LinkagePickerView_lpv_dividerType,
                WheelView.DIVIDER_FILL)))
        setDividerColor(typedArray.getColor(R.styleable.LinkagePickerView_lpv_dividerColor,
                WheelView.DEFAULT_SELECTED_TEXT_COLOR))
        setDividerHeight(typedArray.getDimensionPixelSize(R.styleable.LinkagePickerView_lpv_dividerHeight,
                WheelView.DEFAULT_DIVIDER_HEIGHT))
        setWheelDividerPadding(typedArray.getDimensionPixelSize(R.styleable.LinkagePickerView_lpv_dividerPadding,
                WheelView.DEFAULT_TEXT_PADDING))
        setDividerOffsetY(typedArray.getDimensionPixelOffset(R.styleable.LinkagePickerView_lpv_dividerOffsetY, 0))
        setCurved(typedArray.getBoolean(R.styleable.LinkagePickerView_lpv_curved, true))
        setCurvedArcDirection(WheelView.convertCurvedArcDirection(typedArray.getInt(R.styleable.LinkagePickerView_lpv_curvedArcDirection,
                WheelView.CURVED_ARC_DIRECTION_CENTER)))
        setCurvedArcDirectionFactor(typedArray.getFloat(R.styleable.LinkagePickerView_lpv_curvedArcDirectionFactor,
                WheelView.DEFAULT_CURVED_FACTOR))
        setShowCurtain(typedArray.getBoolean(R.styleable.LinkagePickerView_lpv_showCurtain, false))
        setCurtainColor(typedArray.getColor(R.styleable.LinkagePickerView_lpv_curtainColor, Color.TRANSPARENT))
        typedArray.recycle()
    }

    override fun setTextFormatter(textFormatter: TextFormatter) {
        linkagePickerHelper.setTextFormatter(textFormatter)
    }

    override fun setLinkage1TextFormatter(textFormatter: TextFormatter) {
        linkagePickerHelper.setLinkage1TextFormatter(textFormatter)
    }

    override fun setLinkage2TextFormatter(textFormatter: TextFormatter) {
        linkagePickerHelper.setLinkage2TextFormatter(textFormatter)
    }

    override fun setLinkage3TextFormatter(textFormatter: TextFormatter) {
        linkagePickerHelper.setLinkage3TextFormatter(textFormatter)
    }

    override fun setOnRequestData2Listener(listener: OnRequestData2Listener?) {
        linkagePickerHelper.setOnRequestData2Listener(listener)
    }

    override fun setOnRequestData3Listener(listener: OnRequestData3Listener?) {
        linkagePickerHelper.setOnRequestData3Listener(listener)
    }

    override fun setData(firstData: List<Any>, useSecond: Boolean) {
        linkagePickerHelper.setData(firstData, useSecond)
    }

    override fun setData(firstData: List<Any>, useSecond: Boolean, useThird: Boolean) {
        linkagePickerHelper.setData(firstData, useSecond, useThird)
    }

    override fun setOnScrollChangedListener(listener: OnScrollChangedListener?) {
        linkagePickerHelper.setOnScrollChangedListener(listener)
    }

    override fun setOnLinkageSelectedListener(listener: OnLinkageSelectedListener?) {
        linkagePickerHelper.setOnLinkageSelectedListener(listener)
    }

    override fun setMaxTextWidthMeasureType(measureType: WheelView.MeasureType) {
        linkagePickerHelper.setMaxTextWidthMeasureType(measureType)
    }

    override fun setMaxTextWidthMeasureType(linkage1Type: WheelView.MeasureType,
                                            linkage2Type: WheelView.MeasureType,
                                            linkage3Type: WheelView.MeasureType) {
        linkagePickerHelper.setMaxTextWidthMeasureType(linkage1Type, linkage2Type, linkage3Type)
    }

    override fun getLinkage1WheelView(): WheelView {
        return linkagePickerHelper.getLinkage1WheelView()
    }

    override fun getLinkage2WheelView(): WheelView {
        return linkagePickerHelper.getLinkage2WheelView()
    }

    override fun getLinkage3WheelView(): WheelView {
        return linkagePickerHelper.getLinkage3WheelView()
    }

    override fun setVisibleItems(visibleItems: Int) {
        linkagePickerHelper.setVisibleItems(visibleItems)
    }

    override fun setLineSpacing(lineSpacingPx: Int) {
        linkagePickerHelper.setLineSpacing(lineSpacingPx)
    }

    override fun setLineSpacing(lineSpacingDp: Float) {
        linkagePickerHelper.setLineSpacing(lineSpacingDp)
    }

    override fun setCyclic(isCyclic: Boolean) {
        linkagePickerHelper.setCyclic(isCyclic)
    }

    override fun setTextSize(textSizePx: Int) {
        linkagePickerHelper.setTextSize(textSizePx)
    }

    override fun setTextSize(textSizeSp: Float) {
        linkagePickerHelper.setTextSize(textSizeSp)
    }

    override fun setAutoFitTextSize(autoFit: Boolean) {
        linkagePickerHelper.setAutoFitTextSize(autoFit)
    }

    override fun setMinTextSize(minTextSizePx: Int) {
        linkagePickerHelper.setMinTextSize(minTextSizePx)
    }

    override fun setMinTextSize(minTextSizeSp: Float) {
        linkagePickerHelper.setMinTextSize(minTextSizeSp)
    }

    override fun setTextAlign(textAlign: Paint.Align) {
        linkagePickerHelper.setTextAlign(textAlign)
    }

    override fun setNormalTextColor(@ColorInt textColor: Int) {
        linkagePickerHelper.setNormalTextColor(textColor)
    }

    override fun setNormalTextColorRes(@ColorRes textColorRes: Int) {
        linkagePickerHelper.setNormalTextColorRes(textColorRes)
    }

    override fun setSelectedTextColor(@ColorInt textColor: Int) {
        linkagePickerHelper.setSelectedTextColor(textColor)
    }

    override fun setSelectedTextColorRes(@ColorRes textColorRes: Int) {
        linkagePickerHelper.setSelectedTextColorRes(textColorRes)
    }

    override fun setTextPadding(paddingPx: Int) {
        linkagePickerHelper.setTextPadding(paddingPx)
    }

    override fun setTextPadding(paddingDp: Float) {
        linkagePickerHelper.setTextPadding(paddingDp)
    }

    override fun setTextPaddingLeft(textPaddingLeftPx: Int) {
        linkagePickerHelper.setTextPaddingLeft(textPaddingLeftPx)
    }

    override fun setTextPaddingLeft(textPaddingLeftDp: Float) {
        linkagePickerHelper.setTextPaddingLeft(textPaddingLeftDp)
    }

    override fun setTextPaddingRight(textPaddingRightPx: Int) {
        linkagePickerHelper.setTextPaddingRight(textPaddingRightPx)
    }

    override fun setTextPaddingRight(textPaddingRightDp: Float) {
        linkagePickerHelper.setTextPaddingRight(textPaddingRightDp)
    }

    override fun setTypeface(typeface: Typeface) {
        linkagePickerHelper.setTypeface(typeface)
    }

    override fun setTypeface(typeface: Typeface, isBoldForSelectedItem: Boolean) {
        linkagePickerHelper.setTypeface(typeface, isBoldForSelectedItem)
    }

    override fun setShowDivider(showDivider: Boolean) {
        linkagePickerHelper.setShowDivider(showDivider)
    }

    override fun setDividerColor(@ColorInt dividerColor: Int) {
        linkagePickerHelper.setDividerColor(dividerColor)
    }

    override fun setDividerColorRes(@ColorRes dividerColorRes: Int) {
        linkagePickerHelper.setDividerColorRes(dividerColorRes)
    }

    override fun setDividerHeight(dividerHeightPx: Int) {
        linkagePickerHelper.setDividerHeight(dividerHeightPx)
    }

    override fun setDividerHeight(dividerHeightDp: Float) {
        linkagePickerHelper.setDividerHeight(dividerHeightDp)
    }

    override fun setDividerType(dividerType: WheelView.DividerType) {
        linkagePickerHelper.setDividerType(dividerType)
    }

    override fun setWheelDividerPadding(paddingPx: Int) {
        linkagePickerHelper.setWheelDividerPadding(paddingPx)
    }

    override fun setWheelDividerPadding(paddingDp: Float) {
        linkagePickerHelper.setWheelDividerPadding(paddingDp)
    }

    override fun setDividerCap(cap: Paint.Cap) {
        linkagePickerHelper.setDividerCap(cap)
    }

    override fun setDividerOffsetY(offsetYPx: Int) {
        linkagePickerHelper.setDividerOffsetY(offsetYPx)
    }

    override fun setDividerOffsetY(offsetYDp: Float) {
        linkagePickerHelper.setDividerOffsetY(offsetYDp)
    }

    override fun setShowCurtain(showCurtain: Boolean) {
        linkagePickerHelper.setShowCurtain(showCurtain)
    }

    override fun setCurtainColor(@ColorInt curtainColor: Int) {
        linkagePickerHelper.setCurtainColor(curtainColor)
    }

    override fun setCurtainColorRes(@ColorRes curtainColorRes: Int) {
        linkagePickerHelper.setCurtainColorRes(curtainColorRes)
    }

    override fun setCurved(curved: Boolean) {
        linkagePickerHelper.setCurved(curved)
    }

    override fun setCurvedArcDirection(direction: WheelView.CurvedArcDirection) {
        linkagePickerHelper.setCurvedArcDirection(direction)
    }

    override fun setCurvedArcDirectionFactor(factor: Float) {
        linkagePickerHelper.setCurvedArcDirectionFactor(factor)
    }

    override fun setRefractRatio(ratio: Float) {
        linkagePickerHelper.setRefractRatio(ratio)
    }

    override fun setSoundEffect(soundEffect: Boolean) {
        linkagePickerHelper.setSoundEffect(soundEffect)
    }

    override fun setSoundResource(@RawRes soundRes: Int) {
        linkagePickerHelper.setSoundResource(soundRes)
    }

    override fun setSoundVolume(playVolume: Float) {
        linkagePickerHelper.setSoundVolume(playVolume)
    }

    override fun setResetSelectedPosition(reset: Boolean) {
        linkagePickerHelper.setResetSelectedPosition(reset)
    }

    override fun setCanOverRangeScroll(canOverRange: Boolean) {
        linkagePickerHelper.setCanOverRangeScroll(canOverRange)
    }

    override fun setLeftText(text: CharSequence) {
        linkagePickerHelper.setLeftText(text)
    }

    override fun setLeftText(linkage1Text: CharSequence, linkage2Text: CharSequence, linkage3Text: CharSequence) {
        linkagePickerHelper.setLeftText(linkage1Text, linkage2Text, linkage3Text)
    }

    override fun setRightText(text: CharSequence) {
        linkagePickerHelper.setRightText(text)
    }

    override fun setRightText(linkage1Text: CharSequence, linkage2Text: CharSequence, linkage3Text: CharSequence) {
        linkagePickerHelper.setRightText(linkage1Text, linkage2Text, linkage3Text)
    }

    override fun setLeftTextSize(textSizePx: Int) {
        linkagePickerHelper.setLeftTextSize(textSizePx)
    }

    override fun setLeftTextSize(textSizeSp: Float) {
        linkagePickerHelper.setLeftTextSize(textSizeSp)
    }

    override fun setRightTextSize(textSizePx: Int) {
        linkagePickerHelper.setRightTextSize(textSizePx)
    }

    override fun setRightTextSize(textSizeSp: Float) {
        linkagePickerHelper.setRightTextSize(textSizeSp)
    }

    override fun setLeftTextColor(@ColorInt color: Int) {
        linkagePickerHelper.setLeftTextColor(color)
    }

    override fun setLeftTextColorRes(@ColorRes colorRes: Int) {
        linkagePickerHelper.setLeftTextColorRes(colorRes)
    }

    override fun setRightTextColor(@ColorInt color: Int) {
        linkagePickerHelper.setRightTextColor(color)
    }

    override fun setRightTextColorRes(@ColorRes colorRes: Int) {
        linkagePickerHelper.setRightTextColorRes(colorRes)
    }

    override fun setLeftTextMarginRight(marginRightPx: Int) {
        linkagePickerHelper.setLeftTextMarginRight(marginRightPx)
    }

    override fun setLeftTextMarginRight(marginRightDp: Float) {
        linkagePickerHelper.setLeftTextMarginRight(marginRightDp)
    }

    override fun setRightTextMarginLeft(marginLeftPx: Int) {
        linkagePickerHelper.setRightTextMarginLeft(marginLeftPx)
    }

    override fun setRightTextMarginLeft(marginLeftDp: Float) {
        linkagePickerHelper.setRightTextMarginLeft(marginLeftDp)
    }

    override fun setLeftTextGravity(gravity: Int) {
        linkagePickerHelper.setLeftTextGravity(gravity)
    }

    override fun setRightTextGravity(gravity: Int) {
        linkagePickerHelper.setRightTextGravity(gravity)
    }
}