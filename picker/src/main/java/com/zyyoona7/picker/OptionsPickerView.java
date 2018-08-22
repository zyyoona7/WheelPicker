package com.zyyoona7.picker;

import android.content.Context;
import android.support.annotation.Nullable;
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

    private boolean isResetSelectedPosition;
    private boolean isLinkage;

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
        mOptionsWv1.setCyclic(false);
        mOptionsWv2.setCyclic(false);
        mOptionsWv3.setCyclic(false);
    }

    public void setData(List<T> data) {
        setData(data, null, null);
    }

    public void setData(List<T> data1, List<T> data2) {
        setData(data1, data2, null);
    }

    public void setData(List<T> data1, List<T> data2, List<T> data3) {
        isLinkage = false;
        setDataOrGone(data1, mOptionsWv1);
        setDataOrGone(data2, mOptionsWv2);
        setDataOrGone(data3, mOptionsWv3);
    }

    public void setLinkageData(List<T> linkageData1, List<List<T>> linkageData2) {
        setLinkageData(linkageData1, linkageData2, null);
    }

    public void setLinkageData(List<T> linkageData1, List<List<T>> linkageData2, List<List<List<T>>> linkageData3) {
        if (linkageData1 == null || linkageData1.size() == 0 || linkageData2 == null || linkageData2.size() == 0) {
            return;
        }
        //数据限制，联动需保持 最外层List size一致，及linkageData1.size()==linkageData2.size()==linkageData3.size()
        //其次保持第二层 List size 一致，及linkageData2.get(0).size()==linkageData3.get(0).size()
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

    private void setDataOrGone(List<T> data, WheelView<T> wheelView) {
        if (data != null) {
            wheelView.setData(data);
        } else {
            wheelView.setVisibility(GONE);
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
                    mOptionsWv3.setData(mOptionsData3.get(position).get(0));
                }
            } else if (wheelView.getId() == ID_OPTIONS_WV_2) {
                if (mOptionsData3 != null) {
                    mOptionsWv3.setData(mOptionsData3.get(mOptionsWv1.getSelectedItemPosition()).get(position));
                }
            }
        } else {
            //不联动
        }
    }

    public boolean isResetSelectedPosition() {
        return isResetSelectedPosition;
    }

    public void setResetSelectedPosition(boolean isResetSelectedPosition) {
        this.isResetSelectedPosition = isResetSelectedPosition;
        mOptionsWv1.setResetSelectedPosition(isResetSelectedPosition);
        mOptionsWv2.setResetSelectedPosition(isResetSelectedPosition);
        mOptionsWv3.setResetSelectedPosition(isResetSelectedPosition);
    }
}
