package com.zyyoona7.wheel.formatter

import java.util.*

open class SimpleItemTextFormatter<T>(private val format: String,
                                      private val clazz: Class<T>)
    : ItemTextFormatter {

    @Suppress("UNCHECKED_CAST")
    override fun formatText(item: Any?): String {
        return item?.let {
            if (it.javaClass.isAssignableFrom(clazz)) {
                String.format(Locale.getDefault(), format, text(item as T))
            } else {
                ""
            }
        } ?: ""
    }

    protected open fun text(item:T):String{
        return item.toString()
    }
}