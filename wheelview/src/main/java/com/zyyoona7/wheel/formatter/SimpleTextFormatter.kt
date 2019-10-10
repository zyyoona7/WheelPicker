package com.zyyoona7.wheel.formatter

import java.util.*

/**
 * 通用的[TextFormatter]
 *
 * 如果自定义的实体类需要别的属性则直接重新 [text] 方法即可
 */
open class SimpleTextFormatter<T>
@JvmOverloads constructor(private val format: String = "%s")
    : TextFormatter {

    @Suppress("UNCHECKED_CAST")
    override fun formatText(item: Any?): String {
        return item?.let { data ->
            val value = data as? T
            value?.let {
                String.format(Locale.getDefault(), format, text(it))
            } ?: ""
        } ?: ""
    }

    protected open fun text(item: T): Any {
        return item as Any
    }
}