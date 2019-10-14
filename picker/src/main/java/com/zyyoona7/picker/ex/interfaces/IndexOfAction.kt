package com.zyyoona7.picker.ex.interfaces

interface IndexOfAction<T> {

    fun indexOf(item: T): Int

    fun getItem(index: Int): T?
}