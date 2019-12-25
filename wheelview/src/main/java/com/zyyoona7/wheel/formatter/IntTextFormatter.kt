package com.zyyoona7.wheel.formatter

/**
 * 整型[TextFormatter]
 * @param format 中必须包含并且只能包含一个格式说明符（format specifier）
 * 格式说明符请参照 http://java2s.com/Tutorials/Java/Data_Format/Java_Format_Specifier.htm
 * 如果有多个格式说明符会抛出 java.util.MissingFormatArgumentException: Format specifier '%s'(多出来的说明符)
 */
class IntTextFormatter @JvmOverloads constructor(format: String = DEFAULT_INT_FORMAT)
    : SimpleTextFormatter<Int>(format) {

    companion object {
        const val SINGLE_INT_FORMAT="%d"
        private const val DEFAULT_INT_FORMAT = "%02d"
    }
}