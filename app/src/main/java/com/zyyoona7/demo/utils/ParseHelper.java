package com.zyyoona7.demo.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.zyyoona7.demo.entities.CityEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zyyoona7
 * @version v1.0
 * @since 2018/8/21.
 */
public class ParseHelper {
    private static final String TAG = "ParseHelper";

    public static final String NAME_AREA_LEVEL_2 = "AreaLevel_2.json";
    public static final String NAME_AREA_LEVEL_3 = "AreaLevel_3.json";

    private ParseHelper(){}

    public static void initTwoLevelCityList(Context context, List<CityEntity> pList, List<List<CityEntity>> cList) {

        List<CityEntity> twoLevelList = parseTwoLevelCityList(context);
        pList.addAll(twoLevelList);
        for (CityEntity cityEntity : twoLevelList) {
            List<CityEntity> cityList = new ArrayList<>(1);
            List<CityEntity> subCityList = cityEntity.getDistricts();
            cityList.addAll(subCityList);

            cList.add(cityList);
        }

    }

    public static void initThreeLevelCityList(Context context, List<CityEntity> pList, List<List<CityEntity>> cList, List<List<List<CityEntity>>> aList) {
        List<CityEntity> threeLevelCityList = parseThreeLevelCityList(context);
        pList.addAll(threeLevelCityList);
        for (CityEntity cityEntity : threeLevelCityList) {
            List<CityEntity> cityList = new ArrayList<>(1);
            List<List<CityEntity>> areaList = new ArrayList<>(1);
            List<CityEntity> subCityList = cityEntity.getDistricts();
            for (CityEntity entity : subCityList) {
                cityList.add(entity);
                areaList.add(entity.getDistricts());
            }
            cList.add(cityList);
            aList.add(areaList);
        }
    }

    private static void initCityList(List<CityEntity> parseList, List<CityEntity> pList, List<List<CityEntity>> cList, List<List<List<CityEntity>>> aList, boolean isThreeLevel) {

        if (parseList == null) {
            return;
        }
        pList = parseList;
        for (CityEntity cityEntity : parseList) {
            List<CityEntity> cityList = new ArrayList<>(1);
            if (isThreeLevel) {
                List<List<CityEntity>> areaList = new ArrayList<>(1);
            }
            List<CityEntity> subCityList = cityEntity.getDistricts();
            cityList.addAll(subCityList);

        }
    }

    /**
     * 解析出二级城市列表
     *
     * @param context
     * @return
     */
    public static List<CityEntity> parseTwoLevelCityList(Context context) {
        return parseCityList(context, NAME_AREA_LEVEL_2);
    }

    /**
     * 解析出三级城市列表
     *
     * @param context
     * @return
     */
    public static List<CityEntity> parseThreeLevelCityList(Context context) {
        return parseCityList(context, NAME_AREA_LEVEL_3);
    }

    /**
     * 解析城市数据
     *
     * @param context
     * @param assetFileName
     * @return
     */
    private static List<CityEntity> parseCityList(Context context, String assetFileName) {

        JSONObject jsonObject = AssetUtils.loadJSONAsset(context, assetFileName);
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("districts");
            JSONObject countryJobj = jsonArray.getJSONObject(0);
            String contentString = countryJobj.getString("districts");
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(String.class, new CityEntityStringJsonDeserializer())
                    .create();
            return gson.fromJson(contentString, new TypeToken<List<CityEntity>>() {
            }.getType());
        } catch (JSONException e) {
            Log.d(TAG,"城市列表 JSON 数据解析异常：" + e.getMessage());
        }
        return new ArrayList<>(1);
    }

    public static class CityEntityStringJsonDeserializer implements JsonDeserializer<String> {

        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsString();
            } catch (Exception e) {
                return "";
            }
        }
    }
}
