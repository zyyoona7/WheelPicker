package com.zyyoona7.demo

import android.content.Context
import android.util.AttributeSet
import android.view.View

class GenView<T>(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    private val list:List<T> = arrayListOf()
}