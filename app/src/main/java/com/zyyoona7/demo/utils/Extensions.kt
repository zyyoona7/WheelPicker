package com.zyyoona7.demo.utils

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

fun Context.typeface(path:String):Typeface{
    return Typeface.createFromAsset(assets,path)
}

fun Context.typefaceMedium():Typeface{
    return typeface("fonts/pingfang_medium.ttf")
}

fun Context.typefaceRegular():Typeface{
    return typeface("fonts/pingfang_regular.ttf")
}

fun Context.typefaceLight():Typeface{
    return typeface("fonts/pingfang_light.ttf")
}

/**
 * 短促的震动
 *
 * @param millis 震动时长
 */
fun Context.vibrateShot(millis: Long) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    vibrator?.let {
        if (it.hasVibrator()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(millis, 100))
            } else {
                it.vibrate(millis)
            }
        }
    }
}