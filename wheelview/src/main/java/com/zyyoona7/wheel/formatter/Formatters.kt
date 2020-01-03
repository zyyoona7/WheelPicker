package com.zyyoona7.wheel.formatter

import java.util.*

/**
 * item 文本转换器
 */
interface TextFormatter {

    fun formatText(item: Any?): String
}


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


/**
 * 整型[TextFormatter]
 * @param format 中必须包含并且只能包含一个格式说明符（format specifier）
 * 格式说明符请参照 http://java2s.com/Tutorials/Java/Data_Format/Java_Format_Specifier.htm
 * 如果有多个格式说明符会抛出 java.util.MissingFormatArgumentException: Format specifier '%s'(多出来的说明符)
 */
class IntTextFormatter @JvmOverloads constructor(format: String = DEFAULT_INT_FORMAT)
    : SimpleTextFormatter<Int>(format) {

    companion object {
        const val SINGLE_INT_FORMAT = "%d"
        private const val DEFAULT_INT_FORMAT = "%02d"
    }
}