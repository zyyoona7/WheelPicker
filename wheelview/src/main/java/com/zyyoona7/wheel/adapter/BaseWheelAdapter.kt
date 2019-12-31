package com.zyyoona7.wheel.adapter

/**
 * WheelView Adapter 的基类
 */
abstract class BaseWheelAdapter<T>(data: List<T>? = null) {

    private val dataList: MutableList<T> = mutableListOf()

    init {
        data?.let {
            setData(it)
        }
    }

    fun getItemCount(): Int {
        return dataList.size
    }

    internal fun getItemData(position: Int): T? {
        return if (dataList.size > 0 && position in 0 until dataList.size) dataList[position] else null
    }

    fun setData(data: List<T>) {
        dataList.clear()
        dataList.addAll(data)
    }

    fun getData(): List<T> {
        return dataList
    }

    internal abstract fun getItemText(item: Any?): String

    internal abstract fun getItemTextByIndex(index: Int): String
}