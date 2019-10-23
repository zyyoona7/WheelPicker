package com.zyyoona7.picker

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RawRes
import com.zyyoona7.picker.helper.LinkagePickerHelper
import com.zyyoona7.picker.interfaces.LinkagePicker
import com.zyyoona7.picker.interfaces.WheelPicker
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
        View.inflate(context, R.layout.layout_linkage_picker_view, this)
        val wheelView1 = findViewById<WheelView>(R.id.wheel_view_1)
        val wheelView2 = findViewById<WheelView>(R.id.wheel_view_2)
        val wheelView3 = findViewById<WheelView>(R.id.wheel_view_3)
        linkagePickerHelper = LinkagePickerHelper(wheelView1, wheelView2, wheelView3)
    }

    override fun setTextFormatter(textFormatter: TextFormatter) {
        linkagePickerHelper.setTextFormatter(textFormatter)
    }

    override fun setFirstTextFormatter(textFormatter: TextFormatter) {
        linkagePickerHelper.setFirstTextFormatter(textFormatter)
    }

    override fun setSecondTextFormatter(textFormatter: TextFormatter) {
        linkagePickerHelper.setSecondTextFormatter(textFormatter)
    }

    override fun setThirdTextFormatter(textFormatter: TextFormatter) {
        linkagePickerHelper.setThirdTextFormatter(textFormatter)
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

    override fun getFirstWheelView(): WheelView {
        return linkagePickerHelper.getFirstWheelView()
    }

    override fun getSecondWheelView(): WheelView {
        return linkagePickerHelper.getSecondWheelView()
    }

    override fun getThirdWheelView(): WheelView {
        return linkagePickerHelper.getThirdWheelView()
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

    override fun setTextAlign(@WheelView.TextAlign textAlign: Int) {
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

    override fun setDividerType(@WheelView.DividerType dividerType: Int) {
        linkagePickerHelper.setDividerType(dividerType)
    }

    override fun setDividerPadding(paddingPx: Int) {
        linkagePickerHelper.setDividerPadding(paddingPx)
    }

    override fun setDividerPadding(paddingDp: Float) {
        linkagePickerHelper.setDividerPadding(paddingDp)
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

    override fun setCurvedArcDirection(@WheelView.CurvedArcDirection direction: Int) {
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

    override fun setRightText(text: CharSequence) {
        linkagePickerHelper.setRightText(text)
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