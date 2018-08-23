package com.zyyoona7.picker;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zyyoona7.wheel.WheelView;

import java.util.List;

/**
 * 选项选择器
 *
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/8/21.
 */
public class OptionsPickerView<T> extends LinearLayout implements WheelView.OnItemSelectedListener<T> {

    private static final int ID_OPTIONS_WV_1 = 1;
    private static final int ID_OPTIONS_WV_2 = 2;
    private static final int ID_OPTIONS_WV_3 = 3;

    //WheelView
    private WheelView<T> mOptionsWv1;
    private WheelView<T> mOptionsWv2;
    private WheelView<T> mOptionsWv3;

    //联动数据
    private List<T> mOptionsData1;
    private List<List<T>> mOptionsData2;
    private List<List<List<T>>> mOptionsData3;

    //是否联动
    private boolean isLinkage;
    private boolean isResetSelectedPosition;

    private OnOptionsSelectedListener<T> mOnOptionsSelectedListener;

    public OptionsPickerView(Context context) {
        this(context, null);
    }

    public OptionsPickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptionsPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initViews(context);
    }

    /**
     * 初始化View
     *
     * @param context 上下文
     */
    private void initViews(Context context) {
        setOrientation(HORIZONTAL);
        mOptionsWv1 = new WheelView<>(context);
        mOptionsWv1.setId(ID_OPTIONS_WV_1);
        mOptionsWv2 = new WheelView<>(context);
        mOptionsWv2.setId(ID_OPTIONS_WV_2);
        mOptionsWv3 = new WheelView<>(context);
        mOptionsWv3.setId(ID_OPTIONS_WV_3);

        LinearLayout.LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        addView(mOptionsWv1, layoutParams);
        addView(mOptionsWv2, layoutParams);
        addView(mOptionsWv3, layoutParams);

        mOptionsWv1.setOnItemSelectedListener(this);
        mOptionsWv2.setOnItemSelectedListener(this);
        mOptionsWv3.setOnItemSelectedListener(this);
        mOptionsWv1.setAutoFitTextSize(true);
        mOptionsWv2.setAutoFitTextSize(true);
        mOptionsWv3.setAutoFitTextSize(true);
    }

    /**
     * 设置不联动数据
     *
     * @param data 数据
     */
    public void setData(List<T> data) {
        setData(data, null, null);
    }

    /**
     * 设置不联动数据
     *
     * @param data1 数据1
     * @param data2 数据2
     */
    public void setData(List<T> data1, List<T> data2) {
        setData(data1, data2, null);
    }

    /**
     * 设置不联动数据
     *
     * @param data1 数据1
     * @param data2 数据2
     * @param data3 数据3
     */
    public void setData(List<T> data1, List<T> data2, List<T> data3) {
        isLinkage = false;
        setDataOrGone(data1, mOptionsWv1);
        setDataOrGone(data2, mOptionsWv2);
        setDataOrGone(data3, mOptionsWv3);
    }

    /**
     * 设置数据，如果数据为null隐藏对应的WheelView
     *
     * @param data      数据
     * @param wheelView WheelView
     */
    private void setDataOrGone(List<T> data, WheelView<T> wheelView) {
        if (data != null) {
            wheelView.setData(data);
        } else {
            wheelView.setVisibility(GONE);
        }
    }

    /**
     * 设置联动数据
     *
     * @param linkageData1 联动数据1
     * @param linkageData2 联动数据2
     */
    public void setLinkageData(List<T> linkageData1, List<List<T>> linkageData2) {
        setLinkageData(linkageData1, linkageData2, null);
    }

    /**
     * 设置联动数据
     *
     * @param linkageData1 联动数据1
     * @param linkageData2 联动数据2
     * @param linkageData3 联动数据3
     */
    public void setLinkageData(List<T> linkageData1, List<List<T>> linkageData2, List<List<List<T>>> linkageData3) {
        if (linkageData1 == null || linkageData1.size() == 0 || linkageData2 == null || linkageData2.size() == 0) {
            return;
        }
        //数据限制，联动需保持 最外层List size一致，及linkageData1.size()==linkageData2.size()==linkageData3.size()
        //理论上第二层 linkageData2每一项的size及get(i).size()都要和linkageData3.get(i).size()一致
        if (linkageData1.size() != linkageData2.size()) {
            throw new IllegalArgumentException("linkageData1 and linkageData3 are not the same size.");
        }
        isLinkage = true;
        mOptionsData1 = linkageData1;
        mOptionsData2 = linkageData2;
        if (linkageData3 == null) {
            mOptionsData3 = null;
            mOptionsWv3.setVisibility(GONE);
            //两级联动
            mOptionsWv1.setData(linkageData1);
            mOptionsWv2.setData(linkageData2.get(0));
        } else {
            mOptionsWv3.setVisibility(VISIBLE);
            if (linkageData1.size() != linkageData3.size()) {
                throw new IllegalArgumentException("linkageData1 and linkageData3 are not the same size.");
            }

            for (int i = 0; i < linkageData1.size(); i++) {
                if (linkageData2.get(i).size() != linkageData3.get(i).size()) {
                    throw new IllegalArgumentException("linkageData2 index " + i + " List and linkageData3 index "
                            + i + " List are not the same size.");
                }
            }

            mOptionsData3 = linkageData3;
            //三级联动
            mOptionsWv1.setData(linkageData1);
            mOptionsWv2.setData(linkageData2.get(0));
            mOptionsWv3.setData(linkageData3.get(0).get(0));
            if (isResetSelectedPosition) {
                mOptionsWv1.setSelectedItemPosition(0);
                mOptionsWv2.setSelectedItemPosition(0);
                mOptionsWv3.setSelectedItemPosition(0);
            }
        }

    }

    @Override
    public void onItemSelected(WheelView<T> wheelView, T data, int position) {
        if (isLinkage) {
            //联动
            if (wheelView.getId() == ID_OPTIONS_WV_1) {
                //第一个
                mOptionsWv2.setData(mOptionsData2.get(position));
                if (mOptionsData3 != null) {
                    mOptionsWv3.setData(mOptionsData3.get(position).get(mOptionsWv2.getSelectedItemPosition()));
                }
            } else if (wheelView.getId() == ID_OPTIONS_WV_2) {
                //第二个
                if (mOptionsData3 != null) {
                    mOptionsWv3.setData(mOptionsData3.get(mOptionsWv1.getSelectedItemPosition()).get(position));
                }
            }

            if (mOnOptionsSelectedListener != null) {
                int opt1Pos = mOptionsWv1.getSelectedItemPosition();
                int opt2Pos = mOptionsWv2.getSelectedItemPosition();
                int opt3Pos = mOptionsData3 == null ? -1 : mOptionsWv3.getSelectedItemPosition();
                T opt1Data = mOptionsData1.get(opt1Pos);
                T opt2Data = mOptionsData2.get(opt1Pos).get(opt2Pos);
                T opt3Data = null;
                if (mOptionsData3 != null) {
                    opt3Data = mOptionsData3.get(opt1Pos).get(opt2Pos).get(opt3Pos);
                }
                mOnOptionsSelectedListener.onOptionsSelected(opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data);
            }

        } else {
            //不联动
            if (mOnOptionsSelectedListener != null) {
                boolean isOpt1Shown = mOptionsWv1.getVisibility() == VISIBLE;
                int opt1Pos = isOpt1Shown ? mOptionsWv1.getSelectedItemPosition() : -1;
                boolean isOpt2Shown = mOptionsWv2.getVisibility() == VISIBLE;
                int opt2Pos = isOpt2Shown ? mOptionsWv2.getSelectedItemPosition() : -1;
                boolean isOpt3Shown = mOptionsWv3.getVisibility() == VISIBLE;
                int opt3Pos = isOpt3Shown ? mOptionsWv3.getSelectedItemPosition() : -1;
                T opt1Data = isOpt1Shown ? mOptionsWv1.getSelectedItemData() : null;
                T opt2Data = isOpt2Shown ? mOptionsWv2.getSelectedItemData() : null;
                T opt3Data = isOpt3Shown ? mOptionsWv3.getSelectedItemData() : null;
                mOnOptionsSelectedListener.onOptionsSelected(opt1Pos, opt1Data, opt2Pos, opt2Data, opt3Pos, opt3Data);
            }
        }
    }

    /**
     * 获取联动状态下当数据变化时，是否重置选中下标到第一个
     *
     * @return 是否重置选中下标到第一个
     */
    public boolean isResetSelectedPosition() {
        return isResetSelectedPosition;
    }

    /**
     * 设置联动状态下当数据变化时，是否重置选中下标到第一个
     *
     * @param isResetSelectedPosition 当数据变化时,是否重置选中下标到第一个
     */
    public void setResetSelectedPosition(boolean isResetSelectedPosition) {
        this.isResetSelectedPosition = isResetSelectedPosition;
        mOptionsWv1.setResetSelectedPosition(isResetSelectedPosition);
        mOptionsWv2.setResetSelectedPosition(isResetSelectedPosition);
        mOptionsWv3.setResetSelectedPosition(isResetSelectedPosition);
    }

    /**
     * 设置音效开关
     *
     * @param isSoundEffect 是否开启滚动音效
     */
    public void setSoundEffect(boolean isSoundEffect) {
        mOptionsWv1.setSoundEffect(isSoundEffect);
        mOptionsWv2.setSoundEffect(isSoundEffect);
        mOptionsWv3.setSoundEffect(isSoundEffect);
    }

    /**
     * 设置声音效果资源
     *
     * @param rawResId 声音效果资源 越小效果越好 {@link RawRes}
     */
    public void setSoundEffectResource(@RawRes int rawResId) {
        mOptionsWv1.setSoundEffectResource(rawResId);
        mOptionsWv2.setSoundEffectResource(rawResId);
        mOptionsWv3.setSoundEffectResource(rawResId);
    }

    /**
     * 设置播放音量
     *
     * @param playVolume 播放音量 range 0.0-1.0
     */
    public void setPlayVolume(@FloatRange(from = 0.0, to = 1.0) float playVolume) {
        mOptionsWv1.setPlayVolume(playVolume);
        mOptionsWv2.setPlayVolume(playVolume);
        mOptionsWv3.setPlayVolume(playVolume);
    }

    /**
     * 设置字体大小
     *
     * @param textSize 字体大小
     */
    public void setTextSize(float textSize) {
        setTextSize(textSize, false);
    }

    /**
     * 设置字体大小
     *
     * @param textSize 字体大小
     * @param isSp     单位是否是 sp
     */
    public void setTextSize(float textSize, boolean isSp) {
        mOptionsWv1.setTextSize(textSize, isSp);
        mOptionsWv2.setTextSize(textSize, isSp);
        mOptionsWv3.setTextSize(textSize, isSp);
    }

    /**
     * 设置是否自动调整字体大小，以显示完全
     *
     * @param isAutoFitTextSize 是否自动调整字体大小
     */
    public void setAutoFitTextSize(boolean isAutoFitTextSize) {
        mOptionsWv1.setAutoFitTextSize(isAutoFitTextSize);
        mOptionsWv2.setAutoFitTextSize(isAutoFitTextSize);
        mOptionsWv3.setAutoFitTextSize(isAutoFitTextSize);
    }

    /**
     * 设置当前字体
     *
     * @param typeface 字体
     */
    public void setTypeface(Typeface typeface) {
        mOptionsWv1.setTypeface(typeface);
        mOptionsWv2.setTypeface(typeface);
        mOptionsWv3.setTypeface(typeface);
    }

    /**
     * 设置文字对齐方式
     *
     * @param textAlign 文字对齐方式
     *                  {@link WheelView#TEXT_ALIGN_LEFT}
     *                  {@link WheelView#TEXT_ALIGN_CENTER}
     *                  {@link WheelView#TEXT_ALIGN_RIGHT}
     */
    public void setTextAlign(@WheelView.TextAlign int textAlign) {
        mOptionsWv1.setTextAlign(textAlign);
        mOptionsWv2.setTextAlign(textAlign);
        mOptionsWv3.setTextAlign(textAlign);
    }

    /**
     * 设置未选中条目颜色
     *
     * @param textColorRes 未选中条目颜色资源 {@link ColorRes}
     */
    public void setNormalItemTextColorRes(@ColorRes int textColorRes) {
        setNormalItemTextColor(ContextCompat.getColor(getContext(), textColorRes));
    }

    /**
     * 设置未选中条目颜色
     *
     * @param textColor 未选中条目颜色 {@link ColorInt}
     */
    public void setNormalItemTextColor(@ColorInt int textColor) {
        mOptionsWv1.setNormalItemTextColor(textColor);
        mOptionsWv2.setNormalItemTextColor(textColor);
        mOptionsWv3.setNormalItemTextColor(textColor);
    }

    /**
     * 设置选中条目颜色
     *
     * @param selectedItemTextColorRes 选中条目颜色资源 {@link ColorRes}
     */
    public void setSelectedItemTextColorRes(@ColorRes int selectedItemTextColorRes) {
        setSelectedItemTextColor(ContextCompat.getColor(getContext(), selectedItemTextColorRes));
    }

    /**
     * 设置选中条目颜色
     *
     * @param selectedItemTextColor 选中条目颜色 {@link ColorInt}
     */
    public void setSelectedItemTextColor(@ColorInt int selectedItemTextColor) {
        mOptionsWv1.setSelectedItemTextColor(selectedItemTextColor);
        mOptionsWv2.setSelectedItemTextColor(selectedItemTextColor);
        mOptionsWv3.setSelectedItemTextColor(selectedItemTextColor);
    }

    /**
     * 设置文字距离边界的外边距
     *
     * @param textBoundaryMargin 外边距值
     */
    public void setTextBoundaryMargin(float textBoundaryMargin) {
        setTextBoundaryMargin(textBoundaryMargin, false);
    }

    /**
     * 设置文字距离边界的外边距
     *
     * @param textBoundaryMargin 外边距值
     * @param isDp               单位是否为 dp
     */
    public void setTextBoundaryMargin(float textBoundaryMargin, boolean isDp) {
        mOptionsWv1.setTextBoundaryMargin(textBoundaryMargin, isDp);
        mOptionsWv2.setTextBoundaryMargin(textBoundaryMargin, isDp);
        mOptionsWv3.setTextBoundaryMargin(textBoundaryMargin, isDp);
    }

    /**
     * 设置item间距
     *
     * @param lineSpacing 行间距值
     */
    public void setLineSpacing(float lineSpacing) {
        setLineSpacing(lineSpacing, false);
    }

    /**
     * 设置item间距
     *
     * @param lineSpacing 行间距值
     * @param isDp        lineSpacing 单位是否为 dp
     */
    public void setLineSpacing(float lineSpacing, boolean isDp) {
        mOptionsWv1.setLineSpacing(lineSpacing, isDp);
        mOptionsWv2.setLineSpacing(lineSpacing, isDp);
        mOptionsWv3.setLineSpacing(lineSpacing, isDp);
    }

    /**
     * 设置可见的条目数
     *
     * @param visibleItems 可见条目数
     */
    public void setVisibleItems(int visibleItems) {
        mOptionsWv1.setVisibleItems(visibleItems);
        mOptionsWv2.setVisibleItems(visibleItems);
        mOptionsWv3.setVisibleItems(visibleItems);
    }

    /**
     * 设置是否循环滚动
     *
     * @param isCyclic 是否是循环滚动
     */
    public void setCyclic(boolean isCyclic) {
        mOptionsWv1.setCyclic(isCyclic);
        mOptionsWv2.setCyclic(isCyclic);
        mOptionsWv3.setCyclic(isCyclic);
    }

    /**
     * 设置是否显示分割线
     *
     * @param isShowDivider 是否显示分割线
     */
    public void setShowDivider(boolean isShowDivider) {
        mOptionsWv1.setShowDivider(isShowDivider);
        mOptionsWv2.setShowDivider(isShowDivider);
        mOptionsWv3.setShowDivider(isShowDivider);
    }

    public void setDividerColorRes(@ColorRes int dividerColorRes) {
        setDividerColor(ContextCompat.getColor(getContext(), dividerColorRes));
    }

    /**
     * 设置分割线颜色
     *
     * @param dividerColor 分割线颜色 {@link ColorInt}
     */
    public void setDividerColor(@ColorInt int dividerColor) {
        mOptionsWv1.setDividerColor(dividerColor);
        mOptionsWv2.setDividerColor(dividerColor);
        mOptionsWv3.setDividerColor(dividerColor);
    }

    /**
     * 设置分割线高度
     *
     * @param dividerHeight 分割线高度
     */
    public void setDividerHeight(float dividerHeight) {
        setDividerHeight(dividerHeight, false);
    }

    /**
     * 设置分割线高度
     *
     * @param dividerHeight 分割线高度
     * @param isDp          单位是否是 dp
     */
    public void setDividerHeight(float dividerHeight, boolean isDp) {
        mOptionsWv1.setDividerHeight(dividerHeight, isDp);
        mOptionsWv2.setDividerHeight(dividerHeight, isDp);
        mOptionsWv3.setDividerHeight(dividerHeight, isDp);
    }

    /**
     * 设置分割线填充类型
     *
     * @param dividerType 分割线填充类型
     *                    {@link WheelView#DIVIDER_TYPE_FILL}
     *                    {@link WheelView#DIVIDER_TYPE_WRAP}
     */
    public void setDividerType(@WheelView.DividerType int dividerType) {
        mOptionsWv1.setDividerType(dividerType);
        mOptionsWv2.setDividerType(dividerType);
        mOptionsWv3.setDividerType(dividerType);
    }

    /**
     * 设置自适应分割线类型时的分割线内边距
     *
     * @param dividerPaddingForWrap 分割线内边距
     */
    public void setDividerPaddingForWrap(float dividerPaddingForWrap) {
        setDividerPaddingForWrap(dividerPaddingForWrap, false);
    }

    /**
     * 设置自适应分割线类型时的分割线内边距
     *
     * @param dividerPaddingForWrap 分割线内边距
     * @param isDp                  单位是否是 dp
     */
    public void setDividerPaddingForWrap(float dividerPaddingForWrap, boolean isDp) {
        mOptionsWv1.setDividerPaddingForWrap(dividerPaddingForWrap, isDp);
        mOptionsWv2.setDividerPaddingForWrap(dividerPaddingForWrap, isDp);
        mOptionsWv3.setDividerPaddingForWrap(dividerPaddingForWrap, isDp);
    }

    /**
     * 设置分割线两端形状
     *
     * @param dividerCap 分割线两端形状
     *                   {@link Paint.Cap#BUTT}
     *                   {@link Paint.Cap#ROUND}
     *                   {@link Paint.Cap#SQUARE}
     */
    public void setDividerCap(Paint.Cap dividerCap) {
        mOptionsWv1.setDividerCap(dividerCap);
        mOptionsWv2.setDividerCap(dividerCap);
        mOptionsWv3.setDividerCap(dividerCap);
    }

    /**
     * 设置是否绘制选中区域
     *
     * @param isDrawSelectedRect 是否绘制选中区域
     */
    public void setDrawSelectedRect(boolean isDrawSelectedRect) {
        mOptionsWv1.setDrawSelectedRect(isDrawSelectedRect);
        mOptionsWv2.setDrawSelectedRect(isDrawSelectedRect);
        mOptionsWv3.setDrawSelectedRect(isDrawSelectedRect);
    }

    /**
     * 设置选中区域颜色
     *
     * @param selectedRectColorRes 选中区域颜色资源 {@link ColorRes}
     */
    public void setSelectedRectColorRes(@ColorRes int selectedRectColorRes) {
        setSelectedRectColor(ContextCompat.getColor(getContext(), selectedRectColorRes));
    }

    /**
     * 设置选中区域颜色
     *
     * @param selectedRectColor 选中区域颜色 {@link ColorInt}
     */
    public void setSelectedRectColor(@ColorInt int selectedRectColor) {
        mOptionsWv1.setSelectedRectColor(selectedRectColor);
        mOptionsWv2.setSelectedRectColor(selectedRectColor);
        mOptionsWv3.setSelectedRectColor(selectedRectColor);
    }

    /**
     * 设置是否是弯曲（3D）效果
     *
     * @param isCurved 是否是弯曲（3D）效果
     */
    public void setCurved(boolean isCurved) {
        mOptionsWv1.setCurved(isCurved);
        mOptionsWv2.setCurved(isCurved);
        mOptionsWv3.setCurved(isCurved);
    }

    /**
     * 设置弯曲（3D）效果左右圆弧效果方向
     *
     * @param curvedArcDirection 左右圆弧效果方向
     *                           {@link WheelView#CURVED_ARC_DIRECTION_LEFT}
     *                           {@link WheelView#CURVED_ARC_DIRECTION_CENTER}
     *                           {@link WheelView#CURVED_ARC_DIRECTION_RIGHT}
     */
    public void setCurvedArcDirection(@WheelView.CurvedArcDirection int curvedArcDirection) {
        mOptionsWv1.setCurvedArcDirection(curvedArcDirection);
        mOptionsWv2.setCurvedArcDirection(curvedArcDirection);
        mOptionsWv3.setCurvedArcDirection(curvedArcDirection);
    }

    /**
     * 设置弯曲（3D）效果左右圆弧偏移效果方向系数
     *
     * @param curvedArcDirectionFactor 左右圆弧偏移效果方向系数
     *                                 range 0.0-1.0 越大越明显
     */
    public void setCurvedArcDirectionFactor(@FloatRange(from = 0.0f, to = 1.0f) float curvedArcDirectionFactor) {
        mOptionsWv1.setCurvedArcDirectionFactor(curvedArcDirectionFactor);
        mOptionsWv2.setCurvedArcDirectionFactor(curvedArcDirectionFactor);
        mOptionsWv3.setCurvedArcDirectionFactor(curvedArcDirectionFactor);
    }

    /**
     * 设置选中条目折射偏移比例
     *
     * @param curvedRefractRatio 折射偏移比例 range 0.0-1.0
     */
    public void setCurvedRefractRatio(@FloatRange(from = 0.0f, to = 1.0f) float curvedRefractRatio) {
        mOptionsWv1.setCurvedRefractRatio(curvedRefractRatio);
        mOptionsWv2.setCurvedRefractRatio(curvedRefractRatio);
        mOptionsWv3.setCurvedRefractRatio(curvedRefractRatio);
    }

    /**
     * 获取选项选中回调监听器
     *
     * @return 选项选中回调监听器
     */
    public OnOptionsSelectedListener<T> getOnOptionsSelectedListener() {
        return mOnOptionsSelectedListener;
    }

    /**
     * 设置选项选中回调监听器
     *
     * @param onOptionsSelectedListener 选项选中回调监听器
     */
    public void setOnOptionsSelectedListener(OnOptionsSelectedListener<T> onOptionsSelectedListener) {
        mOnOptionsSelectedListener = onOptionsSelectedListener;
    }

    /**
     * 获取选项1WheelView 选中下标
     *
     * @return 选中下标
     */
    public int getOpt1SelectedPosition() {
        return mOptionsWv1.getSelectedItemPosition();
    }

    /**
     * 设置选项1WheelView 选中下标
     *
     * @param position 选中下标
     */
    public void setOpt1SelectedPosition(int position) {
        setOpt1SelectedPosition(position, false);
    }

    /**
     * 设置选项1WheelView 选中下标
     *
     * @param position       选中下标
     * @param isSmoothScroll 是否平滑滚动
     */
    public void setOpt1SelectedPosition(int position, boolean isSmoothScroll) {
        setOpt1SelectedPosition(position, isSmoothScroll, 0);
    }

    /**
     * 设置选项1WheelView 选中下标
     *
     * @param position       选中下标
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动持续时间
     */
    public void setOpt1SelectedPosition(int position, boolean isSmoothScroll, int smoothDuration) {
        mOptionsWv1.setSelectedItemPosition(position, isSmoothScroll, smoothDuration);
    }

    /**
     * 获取选项2WheelView 选中下标
     *
     * @return 选中下标
     */
    public int getOpt2SelectedPosition() {
        return mOptionsWv2.getSelectedItemPosition();
    }

    /**
     * 设置选项2WheelView 选中下标
     *
     * @param position 选中下标
     */
    public void setOpt2SelectedPosition(int position) {
        setOpt2SelectedPosition(position, false);
    }

    /**
     * 设置选项2WheelView 选中下标
     *
     * @param position       选中下标
     * @param isSmoothScroll 是否平滑滚动
     */
    public void setOpt2SelectedPosition(int position, boolean isSmoothScroll) {
        setOpt2SelectedPosition(position, isSmoothScroll, 0);
    }

    /**
     * 设置选项2WheelView 选中下标
     *
     * @param position       选中下标
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动持续时间
     */
    public void setOpt2SelectedPosition(int position, boolean isSmoothScroll, int smoothDuration) {
        mOptionsWv2.setSelectedItemPosition(position, isSmoothScroll, smoothDuration);
    }

    /**
     * 获取选项3WheelView 选中下标
     *
     * @return 选中下标
     */
    public int getOpt3SelectedPosition() {
        return mOptionsWv3.getSelectedItemPosition();
    }

    /**
     * 设置选项3WheelView 选中下标
     *
     * @param position 选中下标
     */
    public void setOpt3SelectedPosition(int position) {
        setOpt3SelectedPosition(position, false);
    }

    /**
     * 设置选项3WheelView 选中下标
     *
     * @param position       选中下标
     * @param isSmoothScroll 是否平滑滚动
     */
    public void setOpt3SelectedPosition(int position, boolean isSmoothScroll) {
        setOpt3SelectedPosition(position, isSmoothScroll, 0);
    }

    /**
     * 设置选项3WheelView 选中下标
     *
     * @param position       选中下标
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动持续时间
     */
    public void setOpt3SelectedPosition(int position, boolean isSmoothScroll, int smoothDuration) {
        mOptionsWv3.setSelectedItemPosition(position, isSmoothScroll, smoothDuration);
    }

    /**
     * 获取选项1WheelView 选中的数据
     *
     * @return 选中的数据
     */
    public T getOpt1SelectedData() {
        if (isLinkage) {
            return mOptionsData1.get(mOptionsWv1.getSelectedItemPosition());
        } else {
            return mOptionsWv1.getSelectedItemData();
        }
    }

    /**
     * 获取选项2WheelView 选中的数据
     *
     * @return 选中的数据
     */
    public T getOpt2SelectedData() {
        if (isLinkage) {
            return mOptionsData2.get(mOptionsWv1.getSelectedItemPosition())
                    .get(mOptionsWv2.getSelectedItemPosition());
        } else {
            return mOptionsWv2.getSelectedItemData();
        }
    }

    /**
     * 获取选项3WheelView 选中的数据
     *
     * @return 选中的数据
     */
    public T getOpt3SelectedData() {
        if (isLinkage) {
            return mOptionsData3 == null ? null : mOptionsData3.get(mOptionsWv1.getSelectedItemPosition())
                    .get(mOptionsWv2.getSelectedItemPosition())
                    .get(mOptionsWv3.getSelectedItemPosition());
        } else {
            return mOptionsWv3.getSelectedItemData();
        }
    }

    /**
     * 获取选项1WheelView
     *
     * @return 选项1WheelView
     */
    public WheelView<T> getOptionsWv1() {
        return mOptionsWv1;
    }

    /**
     * 获取选项2WheelView
     *
     * @return 选项2WheelView
     */
    public WheelView<T> getOptionsWv2() {
        return mOptionsWv2;
    }

    /**
     * 获取选项3WheelView
     *
     * @return 选项3WheelView
     */
    public WheelView<T> getOptionsWv3() {
        return mOptionsWv3;
    }

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
}
