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

import com.zyyoona7.demo.entities.CityEntity;
import com.zyyoona7.demo.utils.ParseHelper;
import com.zyyoona7.wheel.WheelView;

import java.util.ArrayList;
import java.util.Calendar;
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

    private AppCompatSeekBar mLineSpacingSb;
    private AppCompatButton mSetLineSpacingBtn;

    private SwitchCompat mCurvedSc;
    private RadioGroup mCurvedArcDirectionRg;
    private AppCompatButton mSetCurvedArcDirectionBtn;
    private AppCompatButton mSetCurvedFactorBtn;
    private AppCompatSeekBar mCurvedFactorSb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatButton goCustomAttrsBtn = findViewById(R.id.btn_go_custom_attrs);
        goCustomAttrsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });

        AppCompatButton goPickerBtn=findViewById(R.id.btn_go_picker_view);
        goPickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Main3Activity.class);
                startActivity(intent);
            }
        });

        final WheelView<String> wheelView = findViewById(R.id.wheelview);
//        List<String> list = new ArrayList<>(1);
        List<String> list = new ArrayList<>(1);
        for (int i = 0; i < 20; i++) {
//            list.add("DEFAULT_TEXT" + i);
            list.add(i + "日");
        }
//        wheelView.setIntegerNeedFormat(true);
        wheelView.setOnItemSelectedListener(new WheelView.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(WheelView<String> wheelView, String data, int position) {
                Log.i(TAG, "onItemSelected: data=" + data + ",position=" + position);
            }
        });
        wheelView.setOnWheelChangedListener(new WheelView.OnWheelChangedListener() {
            @Override
            public void onWheelScroll(int scrollOffsetY) {
                Log.d(TAG, "onWheelScroll: scrollOffsetY=" + scrollOffsetY);
            }

            @Override
            public void onWheelItemChanged(int oldPosition, int newPosition) {
                Log.i(TAG, "onWheelItemChanged: oldPosition=" + oldPosition + ",newPosition=" + newPosition);
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

        //经过测试 OGG 格式比 MP3 效果好
        wheelView.setSoundEffectResource(R.raw.button_choose);
        SwitchCompat soundSc = findViewById(R.id.sc_turn_on_sound);
        soundSc.setChecked(wheelView.isSoundEffect());
        soundSc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wheelView.setSoundEffect(isChecked);
            }
        });
        final AppCompatSeekBar soundVolumeSb = findViewById(R.id.sb_sound_effect);
        soundVolumeSb.setMax(100);
        soundVolumeSb.setProgress((int) (wheelView.getPlayVolume() * 100));
        AppCompatButton setSoundVolumeBtn = findViewById(R.id.btn_set_sound_effect);
        setSoundVolumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelView.setPlayVolume(soundVolumeSb.getProgress() * 1.0f / 100);
            }
        });

        SwitchCompat cyclicSc = findViewById(R.id.sc_turn_on_cyclic);
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
                wheelView.setSelectedItemPosition(randomPosition, mSmoothCb.isChecked(), mDurationSb.getProgress());
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

        final AppCompatSeekBar textSizeSb = findViewById(R.id.sb_text_size);
        textSizeSb.setMax(100);
        textSizeSb.setProgress((int) wheelView.getTextSize());
        final AppCompatButton setTextSizeBtn = findViewById(R.id.btn_set_text_size);
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


        mLineSpacingSb = findViewById(R.id.sb_line_spacing);
        mLineSpacingSb.setMax(100);
        mLineSpacingSb.setProgress(30);
        wheelView.setLineSpacing(30);
        mSetLineSpacingBtn = findViewById(R.id.btn_set_line_spacing);
        mSetLineSpacingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelView.setLineSpacing(mLineSpacingSb.getProgress());
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

        mCurvedArcDirectionRg = findViewById(R.id.rg_curved_arc_direction);
        mSetCurvedArcDirectionBtn = findViewById(R.id.btn_set_curved_arc_direction);
        mSetCurvedArcDirectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurvedArcDirectionRg.getCheckedRadioButtonId() == R.id.rb_curved_left) {
                    wheelView.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_LEFT);
                } else if (mCurvedArcDirectionRg.getCheckedRadioButtonId() == R.id.rb_curved_right) {
                    wheelView.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_RIGHT);
                } else {
                    wheelView.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_CENTER);
                }
            }
        });

        mCurvedFactorSb = findViewById(R.id.sb_curved_factor);
        mCurvedFactorSb.setMax(10);
        mCurvedFactorSb.setProgress((int) (wheelView.getCurvedArcDirectionFactor() * 10));
        mSetCurvedFactorBtn = findViewById(R.id.btn_set_curved_factor);
        mSetCurvedFactorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelView.setCurvedArcDirectionFactor(mCurvedFactorSb.getProgress() * 0.1f);
            }
        });

        SwitchCompat dividerSc = findViewById(R.id.sc_turn_on_divider);
        dividerSc.setChecked(wheelView.isShowDivider());
        dividerSc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wheelView.setShowDivider(isChecked);
            }
        });

        final RadioGroup typeRg = findViewById(R.id.rg_divider_type);
        AppCompatButton setTypeBtn = findViewById(R.id.btn_set_divider_type);
        setTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeRg.getCheckedRadioButtonId() == R.id.rb_divider_fill) {
                    wheelView.setDividerType(WheelView.DIVIDER_TYPE_FILL);
                } else {
                    wheelView.setDividerType(WheelView.DIVIDER_TYPE_WRAP);
                }
            }
        });

        final AppCompatSeekBar heightSb = findViewById(R.id.sb_divider_height);
        heightSb.setMax(20);
        heightSb.setProgress((int) wheelView.getDividerHeight());
        AppCompatButton setHeightBtn = findViewById(R.id.btn_set_divider_height);
        setHeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelView.setDividerHeight(heightSb.getProgress());
            }
        });

        final AppCompatSeekBar paddingSb = findViewById(R.id.sb_divider_padding);
        paddingSb.setMax(100);
        paddingSb.setProgress((int) wheelView.getDividerPaddingForWrap());
        AppCompatButton setPaddingBtn = findViewById(R.id.btn_set_divider_padding);
        setPaddingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelView.setDividerPaddingForWrap(paddingSb.getProgress());
            }
        });


        WheelView<CityEntity> cityWv=findViewById(R.id.wv_city);
        //解析城市列表
        List<CityEntity> cityData= ParseHelper.parseTwoLevelCityList(this);
        cityWv.setData(cityData);
    }
}
