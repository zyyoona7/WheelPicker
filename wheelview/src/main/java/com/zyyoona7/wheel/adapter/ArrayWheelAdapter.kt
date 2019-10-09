package com.zyyoona7.wheel.adapter

import com.zyyoona7.wheel.formatter.ItemTextFormatter

open class ArrayWheelAdapter<T> @JvmOverloads constructor(data: List<T>? = null)
    : WheelAdapter<T> {
    private val dataList: MutableList<T> = mutableListOf()
    var itemTextFormatter: ItemTextFormatter? = null
    var formatterBlock:((Any?)->String)?=null
    internal var isCyclic: Boolean = false

    init {
        data?.let {
            setData(it)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): T? {
        return if (position in 0 until dataList.size) dataList[position] else null
    }

    override fun getItemText(item: Any?): String {
        return itemTextFormatter?.formatText(item)
                ?: (formatterBlock?.invoke(item)?:(item?.toString() ?: ""))
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

    fun getData():List<T>{
        return dataList
    }

    fun addData(item: T) {
        dataList.add(item)
    }

    fun addData(data: List<T>) {
        dataList.addAll(data)
    }
}