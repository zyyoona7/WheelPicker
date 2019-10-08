package com.zyyoona7.picker.listener;

import androidx.annotation.Nullable;

/**
 * 选项选中回调
 *
 * @param <T> 泛型
 */
public interface OnOptionsSelectedListener<T> {

    /**
     * 选项选中回调
     *
     * @param opt1Pos  选项1WheelView 选中的下标
     * @param opt1Data 选项1WheelView 选中的下标对应的数据（普通用法第一项无数据返回null）
     * @param opt2Pos  选项2WheelView 选中的下标
     * @param opt2Data 选项2WheelView 选中的下标对应的数据（普通用法第二项无数据返回null）
     * @param opt3Pos  选项3WheelView 选中的下标（两级联动返回 -1）
     * @param opt3Data 选项3WheelView 选中的下标对应的数据（普通用法第三项无数据或者两级联动返回null）
     */
    void onOptionsSelected(int opt1Pos, @Nullable T opt1Data, int opt2Pos,
                           @Nullable T opt2Data, int opt3Pos, @Nullable T opt3Data);
}