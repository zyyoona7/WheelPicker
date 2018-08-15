package com.zyyoona7.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.zyyoona7.wheelpicker.WheelView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private AppCompatCheckBox mSmoothCb;
    private AppCompatSeekBar mDurationSb;
    private AppCompatButton mScrollBtn;

    private RadioGroup mAlignRg;
    private AppCompatButton mSetAlignBtn;

    private AppCompatButton mSetMarginBtn;
    private AppCompatSeekBar mMarginSb;

    private AppCompatButton mSetVisibleItemBtn;
    private AppCompatSeekBar mVisibleItemSb;

    private AppCompatSeekBar mLineSpaceSb;
    private AppCompatButton mSetLineSpaceBtn;

    private SwitchCompat mCurvedSc;
    private RadioGroup mCurvedAlignRg;
    private AppCompatButton mSetCurvedAlignBtn;
    private AppCompatButton mSetCurvedBiasBtn;
    private AppCompatSeekBar mCurvedBiasSb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatButton goCustomAttrsBtn=findViewById(R.id.btn_go_custom_attrs);
        goCustomAttrsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
            }
        });

        final WheelView<Integer> wheelView = findViewById(R.id.wheelview);
//        List<String> list = new ArrayList<>(1);
        List<Integer> list = new ArrayList<>(1);
        for (int i = 0; i < 20; i++) {
//            list.add("DEFAULT_TEXT" + i);
            list.add(i);
        }
//        wheelView.setIntegerNeedFormat(true);
        wheelView.setOnItemSelectedListener(new WheelView.OnItemSelectedListener<Integer>() {
            @Override
            public void onItemSelected(WheelView<Integer> wheelView, Integer data, int position) {
                Log.i(TAG, "onItemSelected: data=" + data + ",position=" + position);
            }
        });
        wheelView.setOnWheelChangedListener(new WheelView.OnWheelChangedListener() {
            @Override
            public void onWheelScroll(int scrollOffsetY) {
                Log.d(TAG, "onWheelScroll: scrollOffsetY=" + scrollOffsetY);
            }

            @Override
            public void onWheelSelected(int position) {
                Log.d(TAG, "onWheelSelected: position=" + position);
            }

            @Override
            public void onWheelScrollStateChanged(int state) {
                Log.i(TAG, "onWheelScrollStateChanged: state=" + state);
            }
        });

        wheelView.setData(list);

        SwitchCompat cyclicSc=findViewById(R.id.sc_turn_on_cyclic);
        cyclicSc.setChecked(wheelView.isCyclic());
        cyclicSc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wheelView.setCyclic(isChecked);
            }
        });

        mSmoothCb = findViewById(R.id.cb_smooth);
        mDurationSb = findViewById(R.id.sb_smooth_duration);
        mDurationSb.setProgress(250);
        mDurationSb.setMax(2000);

        final Random random = new Random();
        mScrollBtn = findViewById(R.id.button_random);
        mScrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int randomPosition = random.nextInt(20);
                Log.d(TAG, "onClick: randomPosition=" + randomPosition);
                mScrollBtn.setText("随机滚动到 " + randomPosition + " 下标");
                wheelView.setCurrentItemPosition(randomPosition, mSmoothCb.isChecked(), mDurationSb.getProgress());
//                wheelView.setCyclic(mSmoothCb.isChecked());
            }
        });

        mAlignRg = findViewById(R.id.rg_align);
        mSetAlignBtn = findViewById(R.id.btn_set_align);
        mSetAlignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int align;
                if (mAlignRg.getCheckedRadioButtonId() == R.id.rb_left) {
                    align = WheelView.TEXT_ALIGN_LEFT;
                } else if (mAlignRg.getCheckedRadioButtonId() == R.id.rb_right) {
                    align = WheelView.TEXT_ALIGN_RIGHT;
                } else {
                    align = WheelView.TEXT_ALIGN_CENTER;
                }
                wheelView.setTextAlign(align);
            }
        });

        final AppCompatSeekBar textSizeSb=findViewById(R.id.sb_text_size);
        textSizeSb.setMax(100);
        textSizeSb.setProgress((int) wheelView.getTextSize());
        final AppCompatButton setTextSizeBtn=findViewById(R.id.btn_set_text_size);
        setTextSizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelView.setTextSize(textSizeSb.getProgress());
            }
        });

        mMarginSb = findViewById(R.id.sb_margin);
        mMarginSb.setMax(500);
        mMarginSb.setProgress(50);
        wheelView.setTextBoundaryMargin(50);
        mSetMarginBtn = findViewById(R.id.btn_set_margin);
        mSetMarginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelView.setTextBoundaryMargin(mMarginSb.getProgress());
            }
        });

        mVisibleItemSb = findViewById(R.id.sb_visible_item);
        mVisibleItemSb.setMax(10);
        mVisibleItemSb.setProgress(3);
        mSetVisibleItemBtn = findViewById(R.id.btn_set_visible_item);
        mSetVisibleItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibleItem = mVisibleItemSb.getProgress();
                if (visibleItem < 2) {
                    mVisibleItemSb.setProgress(3);
                    visibleItem = 3;
                }
                wheelView.setVisibleItems(visibleItem);
            }
        });


        mLineSpaceSb = findViewById(R.id.sb_line_space);
        mLineSpaceSb.setMax(100);
        mLineSpaceSb.setProgress(30);
        wheelView.setLineSpace(30);
        mSetLineSpaceBtn = findViewById(R.id.btn_set_line_space);
        mSetLineSpaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelView.setLineSpace(mLineSpaceSb.getProgress());
            }
        });

        mCurvedSc = findViewById(R.id.sc_turn_on_curved);
        mCurvedSc.setChecked(wheelView.isCurved());
        mCurvedSc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wheelView.setCurved(isChecked);
            }
        });

        mCurvedAlignRg=findViewById(R.id.rg_curved_align);
        mSetCurvedAlignBtn=findViewById(R.id.btn_set_curved_align);
        mSetCurvedAlignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurvedAlignRg.getCheckedRadioButtonId()== R.id.rb_curved_left) {
                    wheelView.setCurvedAlign(WheelView.CURVED_ALIGN_LEFT);
                }else if (mCurvedAlignRg.getCheckedRadioButtonId()==R.id.rb_curved_right){
                    wheelView.setCurvedAlign(WheelView.CURVED_ALIGN_RIGHT);
                }else {
                    wheelView.setCurvedAlign(WheelView.CURVED_ALIGN_CENTER);
                }
            }
        });

        mCurvedBiasSb =findViewById(R.id.sb_curved_bias);
        mCurvedBiasSb.setMax(10);
        mCurvedBiasSb.setProgress((int) (wheelView.getCurvedAlignBias()*10));
        mSetCurvedBiasBtn=findViewById(R.id.btn_set_curved_bias);
        mSetCurvedBiasBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelView.setCurvedAlignBias(mCurvedBiasSb.getProgress()*0.1f);
            }
        });

        SwitchCompat dividerSc=findViewById(R.id.sc_turn_on_divider);
        dividerSc.setChecked(wheelView.isShowDivider());
        dividerSc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wheelView.setShowDivider(isChecked);
            }
        });

        final RadioGroup typeRg=findViewById(R.id.rg_divider_type);
        AppCompatButton setTypeBtn=findViewById(R.id.btn_set_divider_type);
        setTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeRg.getCheckedRadioButtonId()== R.id.rb_divider_fill) {
                    wheelView.setDividerType(WheelView.DIVIDER_TYPE_FILL);
                }else {
                    wheelView.setDividerType(WheelView.DIVIDER_TYPE_WRAP);
                }
            }
        });

        final AppCompatSeekBar heightSb=findViewById(R.id.sb_divider_height);
        heightSb.setMax(20);
        heightSb.setProgress((int) wheelView.getDividerHeight());
        AppCompatButton setHeightBtn=findViewById(R.id.btn_set_divider_height);
        setHeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelView.setDividerHeight(heightSb.getProgress());
            }
        });

        final AppCompatSeekBar paddingSb=findViewById(R.id.sb_divider_padding);
        paddingSb.setMax(100);
        paddingSb.setProgress((int) wheelView.getDividerPaddingForWrap());
        AppCompatButton setPaddingBtn=findViewById(R.id.btn_set_divider_padding);
        setPaddingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelView.setDividerPaddingForWrap(paddingSb.getProgress());
            }
        });
    }
}
