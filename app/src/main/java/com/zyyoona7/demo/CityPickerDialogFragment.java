package com.zyyoona7.demo;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.zyyoona7.demo.entities.CityEntity;
import com.zyyoona7.demo.utils.ParseHelper;
import com.zyyoona7.dialog.impl.EasyDialog;
import com.zyyoona7.picker.listener.OnOptionsSelectedListener;
import com.zyyoona7.picker.listener.OnPickerScrollStateChangedListener;
import com.zyyoona7.picker.OptionsPickerView;
import com.zyyoona7.wheel.WheelView;
import com.zyyoona7.wheel.formatter.TextFormatter;

import java.util.ArrayList;
import java.util.List;

public class CityPickerDialogFragment extends EasyDialog {

    private OptionsPickerView<CityEntity> mOptionsPickerView;
    private OnOptionsSelectedListener<CityEntity> mOptionsSelectedListener;

    public static CityPickerDialogFragment newInstance() {

        Bundle args = new Bundle();

        CityPickerDialogFragment fragment = new CityPickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public CityPickerDialogFragment() {
        setLayoutRes(R.layout.dialog_city_picker)
                .setGravity(Gravity.BOTTOM)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOptionsPickerView = view.findViewById(R.id.opv_city);
        mOptionsPickerView.setLineSpacing(15, true);


        final Button cancelBtn = view.findViewById(R.id.btn_cancel);
        final Button okBtn = view.findViewById(R.id.btn_ok);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOptionsSelectedListener != null) {
                    mOptionsSelectedListener.onOptionsSelected(mOptionsPickerView.getOpt1SelectedPosition(),mOptionsPickerView.getOpt1SelectedData(),
                            mOptionsPickerView.getOpt2SelectedPosition(),mOptionsPickerView.getOpt2SelectedData(),
                            mOptionsPickerView.getOpt3SelectedPosition(),mOptionsPickerView.getOpt3SelectedData());
                }
                dismissAllowingStateLoss();
            }
        });

        mOptionsPickerView.setOnOptionsSelectedListener(new OnOptionsSelectedListener<CityEntity>() {
            @Override
            public void onOptionsSelected(int opt1Pos, @Nullable CityEntity opt1Data, int opt2Pos,
                                          @Nullable CityEntity opt2Data, int opt3Pos, @Nullable CityEntity opt3Data) {

            }
        });

        mOptionsPickerView.setOnPickerScrollStateChangedListener(new OnPickerScrollStateChangedListener() {
            @Override
            public void onScrollStateChanged(int state) {
                okBtn.setEnabled(state == WheelView.SCROLL_STATE_IDLE);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<CityEntity> p3List = new ArrayList<>(1);
        List<List<CityEntity>> c3List = new ArrayList<>(1);
        List<List<List<CityEntity>>> d3List = new ArrayList<>(1);
        ParseHelper.initThreeLevelCityList(getActivity(), p3List, c3List, d3List);
        mOptionsPickerView.setItemTextFormatter(new TextFormatter() {
            @NonNull
            @Override
            public String formatText(Object item) {
                if (item instanceof CityEntity) {
                    return  ((CityEntity) item).getName();
                }
                return "";
            }
        });
        mOptionsPickerView.setLinkageData(p3List, c3List, d3List);
    }

    public CityPickerDialogFragment setOnSelectedListener(OnOptionsSelectedListener<CityEntity> listener){
        this.mOptionsSelectedListener=listener;
        return this;
    }
}
