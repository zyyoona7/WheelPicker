package com.zyyoona7.wheel.adapter

import com.zyyoona7.wheel.formatter.ItemTextFormatter

open class BaseWheelAdapter<T> : WheelAdapter<T> {
    private val dataList: MutableList<T> = mutableListOf()
    var itemTextFormatter: ItemTextFormatter? = null
    var isCyclic: Boolean = false

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): T? {
        return if (position in 0 until dataList.size) dataList[position] else null
    }

    override fun getItemText(item: T?): String {
        return itemTextFormatter?.formatText(item) ?: (item?.toString()?:"")
    }

    override fun getItemTextByIndex(index: Int): String {
        val dataSize = getItemCount()
        if (dataSize == 0) {
            return ""
        }

        if (isCyclic) {
            var i = index % dataSize
            if (i < 0) {
                i += dataSize
            }
            return getItemText(getItem(i))
        } else {
            if (index in 0 until dataSize) {
                return getItemText(getItem(index))
            }
        }
        return ""
    }

    fun setData(data: List<T>) {
        dataList.clear()
        dataList.addAll(data)
    }

    fun addData(item: T) {
        dataList.add(item)
    }

    fun addData(data: List<T>) {
        dataList.addAll(data)
    }
}