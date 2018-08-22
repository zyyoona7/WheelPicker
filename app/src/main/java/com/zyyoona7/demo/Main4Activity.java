package com.zyyoona7.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zyyoona7.demo.entities.CityEntity;
import com.zyyoona7.demo.utils.ParseHelper;
import com.zyyoona7.picker.OptionsPickerView;

import java.util.ArrayList;
import java.util.List;

public class Main4Activity extends AppCompatActivity {

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
            linkageList5.add("Linkage3 data "+i);
        }
        for (int i = 0; i < 4; i++) {
            linkageList6.add(linkageList5);
        }
        for (int i = 0; i < 10; i++) {
            linkageList2.add(linkageList4);
            linkageList3.add(linkageList6);
        }

        OptionsPickerView<String> opv1 = findViewById(R.id.opv_first);
        opv1.setData(list1,null,list3);
//        opv1.setLinkageData(linkageList1, linkageList2);
        OptionsPickerView<String> opv2 = findViewById(R.id.opv_second);
        opv2.setData(list1, list2, list3);
//        opv2.setLinkageData(linkageList1,linkageList2,linkageList3);

        List<CityEntity> p2List = new ArrayList<>(1);
        List<List<CityEntity>> c2List = new ArrayList<>(1);
        ParseHelper.initTwoLevelCityList(this, p2List, c2List);
        OptionsPickerView<CityEntity> towLinkageOpv = findViewById(R.id.opv_two_linkage);
        towLinkageOpv.setLinkageData(p2List, c2List);

        List<CityEntity> p3List = new ArrayList<>(1);
        List<List<CityEntity>> c3List = new ArrayList<>(1);
        List<List<List<CityEntity>>> d3List = new ArrayList<>(1);
        ParseHelper.initThreeLevelCityList(this, p3List, c3List, d3List);
        OptionsPickerView<CityEntity> threeLinkageOpv = findViewById(R.id.opv_three_linkage);
        threeLinkageOpv.setLinkageData(p3List, c3List, d3List);


    }
}
