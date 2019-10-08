package com.zyyoona7.wheel.formatter;

import androidx.annotation.NonNull;

import java.util.Locale;

public class IntegerItemTextFormatter implements ItemTextFormatter {
    private static final String DEFAULT_INTEGER_FORMAT = "%02d";

    private String format;

    /**
     * @param format 中必须包含并且只能包含一个格式说明符（format specifier）
     *               格式说明符请参照 http://java2s.com/Tutorials/Java/Data_Format/Java_Format_Specifier.htm
     *               <p>
     *               如果有多个格式说明符会抛出 java.util.MissingFormatArgumentException: Format specifier '%s'(多出来的说明符)
     */
    public IntegerItemTextFormatter(String format) {
        this.format = format;
    }

    public IntegerItemTextFormatter() {
        this.format = DEFAULT_INTEGER_FORMAT;
    }

    @NonNull
    @Override
    public String formatText(Object item) {
        if (item instanceof Integer) {
            return String.format(Locale.getDefault(), format, item);
        }
        return "";
    }
}