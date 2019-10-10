package com.zyyoona7.demo.utils

import android.content.Context
import android.graphics.Typeface

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