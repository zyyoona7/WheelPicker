package com.zyyoona7.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zyyoona7.wheelpicker.WheelView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WheelView<String> wheelView=findViewById(R.id.wheelview);
        List<String> list=new ArrayList<>(1);
        for (int i = 0; i < 20; i++) {
            list.add("DEFAULT_TEXT");
        }
        wheelView.setDataList(list);
    }
}
