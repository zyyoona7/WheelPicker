package com.zyyoona7.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zyyoona7.demo.entities.CityEntity;
import com.zyyoona7.demo.utils.ParseHelper;
import com.zyyoona7.picker.OptionsPickerView;
import com.zyyoona7.picker.listener.OnOptionsSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class Main4Activity extends AppCompatActivity {

    private static final String TAG = "Main4Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        List<String> list1 = new ArrayList<>(1);
        List<String> list2 = new ArrayList<>(1);
        List<String> list3 = new ArrayList<>(1);
        for (int i = 0; i < 10; i++) {
            list1.add("zyyoona7__" + i);
        }
        for (int i = 0; i < 5; i++) {
            list2.add("github__" + i);
        }

        for (int i = 0; i < 7; i++) {
            list3.add("WheelPicker__" + i);
        }
        List<String> linkageList1 = new ArrayList<>(1);
        List<List<String>> linkageList2 = new ArrayList<>(1);
        List<List<List<String>>> linkageList3 = new ArrayList<>(1);
        for (int i = 0; i < 10; i++) {
            linkageList1.add("Linkage1 data " + i);
        }

        List<String> linkageList4 = new ArrayList<String>(1);
        List<String> linkageList5 = new ArrayList<>(1);
        List<List<String>> linkageList6 = new ArrayList<>(1);
        for (int i = 0; i < 5; i++) {
            linkageList4.add("Linkage2 data " + i);
        }
        for (int i = 0; i < 3; i++) {
            linkageList5.add("Linkage3 data " + i);
        }
        for (int i = 0; i < 4; i++) {
            linkageList6.add(linkageList5);
        }
        for (int i = 0; i < 10; i++) {
            linkageList2.add(linkageList4);
            linkageList3.add(linkageList6);
        }

        OptionsPickerView<String> opv1 = findViewById(R.id.opv_first);
        opv1.setData(list1, null, list3);
        opv1.setTextSize(18,true);
//        opv1.setLinkageData(linkageList1, linkageList2);
        opv1.setOnOptionsSelectedListener(new OnOptionsSelectedListener<String>() {
            @Override
            public void onOptionsSelected(int opt1Pos, @Nullable String opt1Data, int opt2Pos,
                                          @Nullable String opt2Data, int opt3Pos, @Nullable String opt3Data) {
                if (opt1Data == null || opt3Data == null) {
                    return;
                }
                Log.d(TAG, "onOptionsSelected: two Linkage op1Pos=" + opt1Pos + ",op1Data=" + opt1Data + ",op2Pos=" + opt2Pos
                        + ",op2Data=" + opt2Data + ",op3Pos=" + opt3Pos + ",op3Data=" + opt3Data);
            }
        });
        OptionsPickerView<String> opv2 = findViewById(R.id.opv_second);
        opv2.setData(list1, list2, list3);
        opv2.setTextSize(18,true);
//        opv2.setLinkageData(linkageList1,linkageList2,linkageList3);
        opv2.setOnOptionsSelectedListener(new OnOptionsSelectedListener<String>() {
            @Override
            public void onOptionsSelected(int opt1Pos, @Nullable String opt1Data, int opt2Pos,
                                          @Nullable String opt2Data, int opt3Pos, @Nullable String opt3Data) {
                if (opt1Data == null || opt2Data == null || opt3Data == null) {
                    return;
                }
                Log.d(TAG, "onOptionsSelected: two Linkage op1Pos=" + opt1Pos + ",op1Data=" + opt1Data + ",op2Pos=" + opt2Pos
                        + ",op2Data=" + opt2Data + ",op3Pos=" + opt3Pos + ",op3Data=" + opt3Data);
            }
        });

        List<CityEntity> p2List = new ArrayList<>(1);
        List<List<CityEntity>> c2List = new ArrayList<>(1);
        ParseHelper.initTwoLevelCityList(this, p2List, c2List);
        OptionsPickerView<CityEntity> towLinkageOpv = findViewById(R.id.opv_two_linkage);
        towLinkageOpv.setLinkageData(p2List, c2List);
        towLinkageOpv.setTextSize(24f,true);
        towLinkageOpv.setShowDivider(true);
        towLinkageOpv.setCurvedRefractRatio(0.95f);

        towLinkageOpv.setOnOptionsSelectedListener(new OnOptionsSelectedListener<CityEntity>() {
            @Override
            public void onOptionsSelected(int opt1Pos, @Nullable CityEntity opt1Data, int opt2Pos,
                                          @Nullable CityEntity opt2Data, int opt3Pos, @Nullable CityEntity opt3Data) {
                if (opt1Data == null || opt2Data == null) {
                    return;
                }
                Log.d(TAG, "onOptionsSelected: two Linkage op1Pos=" + opt1Pos + ",op1Data=" + opt1Data.getName() + ",op2Pos=" + opt2Pos
                        + ",op2Data=" + opt2Data.getName() + ",op3Pos=" + opt3Pos + ",op3Data=" + opt3Data);
            }
        });

        List<CityEntity> p3List = new ArrayList<>(1);
        List<List<CityEntity>> c3List = new ArrayList<>(1);
        List<List<List<CityEntity>>> d3List = new ArrayList<>(1);
        ParseHelper.initThreeLevelCityList(this, p3List, c3List, d3List);
        final OptionsPickerView<CityEntity> threeLinkageOpv = findViewById(R.id.opv_three_linkage);
        threeLinkageOpv.setLinkageData(p3List, c3List, d3List);
        threeLinkageOpv.setVisibleItems(7);
        threeLinkageOpv.setResetSelectedPosition(true);
        threeLinkageOpv.setDrawSelectedRect(true);
        threeLinkageOpv.setSelectedRectColor(Color.parseColor("#D3D3D3"));
        threeLinkageOpv.setNormalItemTextColor(Color.parseColor("#808080"));
        threeLinkageOpv.setTextSize(22f,true);
        threeLinkageOpv.setSoundEffect(true);
        threeLinkageOpv.setSoundEffectResource(R.raw.button_choose);

        threeLinkageOpv.setOnOptionsSelectedListener(new OnOptionsSelectedListener<CityEntity>() {
            @Override
            public void onOptionsSelected(int opt1Pos, @Nullable CityEntity opt1Data, int opt2Pos,
                                          @Nullable CityEntity opt2Data, int opt3Pos, @Nullable CityEntity opt3Data) {
                if (opt1Data == null || opt2Data == null || opt3Data == null) {
                    return;
                }
                Log.d(TAG, "onOptionsSelected: three Linkage op1Pos=" + opt1Pos + ",op1Data=" + opt1Data.getName() + ",op2Pos=" + opt2Pos
                        + ",op2Data=" + opt2Data.getName() + ",op3Pos=" + opt3Pos + ",op3Data=" + opt3Data.getName());
            }
        });

        final AppCompatCheckBox smoothCb = findViewById(R.id.cb_smooth);
        final AppCompatSeekBar smoothDurationSb = findViewById(R.id.sb_smooth_duration);
        smoothDurationSb.setMax(3000);
        smoothDurationSb.setProgress(250);
        final AppCompatEditText opt1Et = findViewById(R.id.et_opt1);
        AppCompatButton setOpt1Btn = findViewById(R.id.btn_set_opt1);
        final AppCompatEditText opt2Et = findViewById(R.id.et_opt2);
        AppCompatButton setOpt2Btn = findViewById(R.id.btn_set_opt2);
        final AppCompatEditText opt3Et = findViewById(R.id.et_opt3);
        AppCompatButton setOpt3Btn = findViewById(R.id.btn_set_opt3);

        setOpt1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String opt1Index = opt1Et.getText().toString();
                if (!TextUtils.isEmpty(opt1Index)) {
                    threeLinkageOpv.setOpt1SelectedPosition(Integer.parseInt(opt1Index), smoothCb.isChecked(), smoothDurationSb.getProgress());
                }
            }
        });

        setOpt2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String opt2Index = opt2Et.getText().toString();
                if (!TextUtils.isEmpty(opt2Index)) {
                    threeLinkageOpv.setOpt2SelectedPosition(Integer.parseInt(opt2Index), smoothCb.isChecked(), smoothDurationSb.getProgress());
                }
            }
        });

        setOpt3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String opt3Index = opt3Et.getText().toString();
                if (!TextUtils.isEmpty(opt3Index)) {
                    threeLinkageOpv.setOpt3SelectedPosition(Integer.parseInt(opt3Index), smoothCb.isChecked(), smoothDurationSb.getProgress());
                }
            }
        });

        Button dialogBtn=findViewById(R.id.btn_dialog);
        dialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityPickerDialogFragment.newInstance()
                        .setOnSelectedListener(new OnOptionsSelectedListener<CityEntity>() {
                            @Override
                            public void onOptionsSelected(int opt1Pos, @Nullable CityEntity opt1Data,
                                                          int opt2Pos, @Nullable CityEntity opt2Data,
                                                          int opt3Pos, @Nullable CityEntity opt3Data) {
                                Toast.makeText(Main4Activity.this,
                                        opt1Data.getWheelText()+","+opt2Data.getWheelText()+","+opt3Data.getWheelText(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show(getSupportFragmentManager());
            }
        });
    }
}
