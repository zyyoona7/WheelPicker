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

    fun setLineSpacing(lineSpacing: Float)

    fun setLineSpacing(lineSpacing: Float, isDp: Boolean)

    fun setCyclic(isCyclic: Boolean)

    /*
      ---------- WheelView 文字相关设置 ----------
     */

    fun setTextSize(textSize: Float)

    fun setTextSize(textSize: Float, isSp: Boolean)

    fun setAutoFitTextSize(autoFit: Boolean)

    fun setTextAlign(@WheelView.TextAlign textAlign: Int)

    fun setNormalTextColor(@ColorInt textColor: Int)

    fun setNormalTextColorRes(@ColorRes textColorRes: Int)

    fun setSelectedTextColor(@ColorInt textColor: Int)

    fun setSelectedTextColorRes(@ColorRes textColorRes: Int)

    fun setTextMargins(margin: Float)

    fun setTextMargins(margin: Float, isDp: Boolean)

    fun setTextMarginLeft(marginLeft:Float)

    fun setTextMarginLeft(marginLeft: Float,isDp: Boolean)

    fun setTextMarginRight(marginRight:Float)

    fun setTextMarginRight(marginRight: Float, isDp: Boolean)

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

    fun setDividerHeight(dividerHeight: Float)

    fun setDividerHeight(dividerHeight: Float, isDp: Boolean)

    fun setDividerType(@WheelView.DividerType dividerType: Int)

    fun setDividerPaddingForWrap(padding: Float)

    fun setDividerPaddingForWrap(padding: Float, isDp: Boolean)

    fun setDividerCap(cap: Paint.Cap)

    fun setDividerOffsetY(offsetY: Float)

    fun setDividerOffsetY(offsetY: Float, isDp: Boolean)
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
}