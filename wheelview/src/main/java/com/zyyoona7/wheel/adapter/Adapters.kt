package com.zyyoona7.wheel.adapter

import com.zyyoona7.wheel.formatter.TextFormatter

/**
 * WheelView Adapter 的基类
 *
 * @author zyyoona7
 */
abstract class BaseWheelAdapter<T>(data: List<T>? = null) {

    private val dataList: MutableList<T> = mutableListOf()
    private var isRangeData: Boolean = false
    internal var isCyclic: Boolean = false
    private val rangeDataList: MutableList<T> by lazy { mutableListOf<T>() }

    init {
        data?.let {
            setData(it)
        }
    }

    fun getItemCount(): Int {
        return if (!isCyclic && isRangeData) rangeDataList.size else dataList.size
    }

    internal fun getRealItemCount(): Int {
        return dataList.size
    }

    internal fun getItemData(position: Int): T? {
        return if (!isCyclic && isRangeData){
            if (inRange(position,rangeDataList)) rangeDataList[position] else null
        }else{
            if (inRange(position,dataList)) dataList[position] else null
        }
    }

    fun setData(data: List<T>) {
        dataList.clear()
        dataList.addAll(data)
    }

    fun setDataRange(min: Int, max: Int) {
        if (min < 0 || max < 0 || min > max
                || !inRange(min, dataList) || !inRange(max, dataList)) {
            isRangeData = false
            rangeDataList.clear()
            return
        }
        isRangeData = true
        rangeDataList.clear()
        for (pos in min..max){
            rangeDataList.add(dataList[pos])
        }
    }

    fun getData(): List<T> {
        return if (!isCyclic && isRangeData) rangeDataList else dataList
    }

    protected fun inRange(position: Int, dataList: List<T>): Boolean {
        return dataList.isNotEmpty() && position in dataList.indices
    }

    internal abstract fun getItemText(item: Any?): String

    internal abstract fun getItemTextByIndex(index: Int): String
}


/**
 * WheelView Adapter 的实现类
 *
 * @author zyyoona7
 */
open class ArrayWheelAdapter<T> @JvmOverloads constructor(data: List<T>? = null)
    : BaseWheelAdapter<T>(data) {

    internal var textFormatter: TextFormatter? = null
    internal var formatterBlock: ((Any?) -> String)? = null
    internal var finishScrollCallback: OnFinishScrollCallback? = null
    internal var selectedItemPosition: Int = 0
    internal var itemIndexer: ItemIndexer? = null
    internal var itemIndexerBlock: ((ArrayWheelAdapter<*>, Any?) -> Int)? = null

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
     * 根据item参数信息查找所在的下标
     */
    @JvmOverloads
    fun indexOf(item: Any?, isCompareFormatText: Boolean = false): Int {
        return itemIndexer?.indexOf(this, item)
                ?: (itemIndexerBlock?.invoke(this, item)
                        ?: internalIndexOf(item, isCompareFormatText))
    }

    private fun internalIndexOf(item: Any?, isCompareFormatText: Boolean): Int {
        for (i in getData().indices) {
            if (isCompareFormatText) {
                if (getItemTextByIndex(i) == item) {
                    return i
                }
            } else {
                if (getData()[i] == item) {
                    return i
                }
            }
        }
        return -1
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


/**
 * item 索引器，用来自定义 indexOf 逻辑
 *
 * @author zyyoona7
 */
interface ItemIndexer {

    fun indexOf(adapter: ArrayWheelAdapter<*>, item: Any?): Int
}