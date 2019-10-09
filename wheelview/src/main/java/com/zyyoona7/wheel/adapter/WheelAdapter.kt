package com.zyyoona7.wheel.adapter

internal interface WheelAdapter<T> {

    fun getItemCount(): Int

    fun getItem(position: Int): T?

    fun getItemText(item: Any?): String

    fun getItemTextByIndex(index: Int): String
}