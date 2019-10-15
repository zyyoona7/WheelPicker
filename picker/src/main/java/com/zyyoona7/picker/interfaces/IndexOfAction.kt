package com.zyyoona7.picker.interfaces

interface IndexOfAction<T> {

    fun indexOf(item: T): Int

    fun getItem(index: Int): T?
}