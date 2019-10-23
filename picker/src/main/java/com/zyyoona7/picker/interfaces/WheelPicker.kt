package com.zyyoona7.picker.interfaces

import android.graphics.Paint
import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RawRes
import com.zyyoona7.wheel.WheelView

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

    fun setTextAlign(@WheelView.TextAlign textAlign: Int)

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

    fun setDividerType(@WheelView.DividerType dividerType: Int)

    fun setDividerPadding(paddingPx: Int)

    fun setDividerPadding(paddingDp: Float)

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

    fun setCurvedArcDirection(@WheelView.CurvedArcDirection direction: Int)

    fun setCurvedArcDirectionFactor(factor: Float)

    fun setRefractRatio(ratio: Float)
    /*
      ---------- WheelView 3D 效果相关设置 ----------
     */

    fun setSoundEffect(soundEffect: Boolean)

    fun setSoundResource(@RawRes soundRes: Int)

    fun setSoundVolume(playVolume: Float)

    fun setResetSelectedPosition(reset: Boolean)

    fun setCanOverRangeScroll(canOverRange: Boolean)

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