package com.zyyoona7.wheel.adapter

import com.zyyoona7.wheel.formatter.TextFormatter

/**
 * WheelView Adapter 的实现类
 */
open class ArrayWheelAdapter<T> @JvmOverloads constructor(data: List<T>? = null)
    : BaseWheelAdapter<T>(data) {

    internal var textFormatter: TextFormatter? = null
    internal var formatterBlock: ((Any?) -> String)? = null
    internal var finishScrollCallback: OnFinishScrollCallback? = null
    internal var selectedItemPosition: Int = 0
    internal var isCyclic: Boolean = false

    override fun getItemText(item: Any?): String {
        return textFormatter?.formatText(item)
                ?: (formatterBlock?.invoke(item) ?: (item?.toString() ?: ""))
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
            return getItemText(getItemData(i))
        } else {
            if (index in 0 until dataSize) {
                return getItemText(getItemData(index))
            }
        }
        return ""
    }

    /**
     * 根据下标获取条目数据
     */
    @Suppress("UNCHECKED_CAST")
    fun <V> getItem(position: Int): V? {
        return getItemData(position) as? V
    }

    /**
     * 获取选中条目下标
     */
    fun getSelectedPosition(): Int {
        finishScrollCallback?.onFinishScroll()
        return selectedItemPosition
    }

    /**
     * 获取选中条目数据
     */
    fun <V> getSelectedItem(): V? {
        finishScrollCallback?.onFinishScroll()
        return getItem(selectedItemPosition)
    }

    /**
     * 获取数据列表
     */
    @Suppress("UNCHECKED_CAST")
    fun <V> getDataList(): List<V>? {
        return getData() as? List<V>
    }

    internal interface OnFinishScrollCallback {

        fun onFinishScroll()
    }
}