![](https://github.com/zyyoona7/WheelPicker/blob/master/perview/banner.png)

![](https://img.shields.io/badge/platform-android-brightgreen.svg)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![](https://img.shields.io/badge/pickerview-1.0.4-brightgreen.svg)](https://bintray.com/zyyoona7/maven/pickerview)
[![](https://img.shields.io/badge/wheelview-1.0.4-brightgreen.svg)](https://bintray.com/zyyoona7/maven/wheelview)
[![](https://img.shields.io/github/license/zyyoona7/WheelPicker.svg)](https://github.com/zyyoona7/WheelPicker#license)
### 简介（Introduction）

自定义 View 实现滑动流畅、功能齐全、用法简单、高度自定义的 WheelView，并在 WheelView 基础之上封装了常用的日期选择器（包括年、月、日 WheelView）、选项选择器。

### 特性（Features）
#### 1. WheelView Features

- 如丝般顺滑的滚动效果，无论快速滚动还是缓慢滚动
- 灵活数据设置，通过泛型设置数据类型，灵活、安全
- 支持类似 iOS 的滚动变化音效
- 支持类似 iOS 的 3D 效果
- 3D 效果下，支持圆弧偏移效果，使其看起来更加立体
- 支持嵌套滚动、循环滚动
- 丰富的滑动监听，支持选中监听、滚动状态改变监听等
- 两种分割线类型可以设置，还有其他分割线骚操作
- 支持自动调整字体大小以使得长文字显示完全
- 支持设置显示条目、设置字体大小、设置字体、设置行间距等常规操作
- 更多自定义操作尽在其中

#### 2. DatePickerView Features

- 支持年月日，年月，月日的日期选择
- 支持格式化数据，可以为 item 显示指定格式化的数据
- 拥有 WheelView 特性，可单独设置每个 WheelView 的效果

#### 3. OptionsPickerView Features

- 支持联动包括二级联动和三级联动，设置数据时把控严格，避免滚动时数据异常

### 效果图（Preview）

#### WheelView Preview

![WheelView 1](https://github.com/zyyoona7/WheelPicker/blob/master/perview/wheel_view_1.gif)
①  ②
![WheelView 2](https://github.com/zyyoona7/WheelPicker/blob/master/perview/wheel_view_2.gif)
<br><br>
![WheelView 3](https://github.com/zyyoona7/WheelPicker/blob/master/perview/wheel_view_3.gif)
③  ④
![WheelView 4](https://github.com/zyyoona7/WheelPicker/blob/master/perview/wheel_view_4.gif)

#### DatePickerView Preview

![DatePickerView_1](https://github.com/zyyoona7/WheelPicker/blob/master/perview/date_picker_view_1.gif)
①  ②
![DatePickerView_2](https://github.com/zyyoona7/WheelPicker/blob/master/perview/date_picker_view_2.gif)

#### OptionsPickerView Preview

![OptionsPickerView_1](https://github.com/zyyoona7/WheelPicker/blob/master/perview/options_picker_view_1.gif)
①  ②
![OptionsPickerView_2](https://github.com/zyyoona7/WheelPicker/blob/master/perview/options_picker_view_2.gif)

### 使用（Usage）

#### WheelView Usage

#### 1.依赖（dependency）

```groovy
    implementation 'com.github.zyyoona7:wheelview:1.0.4'
```

#### 2.基本用法（Basic Usage）
在布局文件中添加
```xml
    <com.zyyoona7.wheel.WheelView
        android:id="@+id/wheelview"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
```
在代码中
```java
    //泛型为数据类型
    final WheelView<Integer> wheelView = findViewById(R.id.wheelview);
    //初始化数据
    List<Integer> list = new ArrayList<>(1);
    for (int i = 0; i < 20; i++) {
        list.add(i);
    }
    //设置数据
    wheelView.setData(list);

    //尽请使用各种方法
    wheelView.setTextSize(24f,true);
    //more...
```
#### 2.进阶用法（Advanced Usage）

> 问：我已经有了创建好的实体怎么办？

> 答：好办~

**我已城市列表为例（其他实体同理）**

我的城市列表实体是这样的：
```java 
public class CityEntity implements IWheelEntity, Serializable {

    //国家
    public static final String LEVEL_COUNTRY = "country";
    //省
    public static final String LEVEL_PROVINCE = "province";
    //市
    public static final String LEVEL_CITY = "city";
    //区
    public static final String LEVEL_DISTRICT = "district";

    private String citycode;
    private String adcode;
    private String name;
    private String center;
    private String level;
    private List<CityEntity> districts;

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<CityEntity> getDistricts() {
        return districts == null ? new ArrayList<CityEntity>(1) : districts;
    }

    public void setDistricts(List<CityEntity> districts) {
        this.districts = districts;
    }

    @Override
    public String toString() {
        return "CityEntity{" +
                "citycode='" + citycode + '\'' +
                ", adcode='" + adcode + '\'' +
                ", name='" + name + '\'' +
                ", center='" + center + '\'' +
                ", level='" + level + '\'' +
                ", districts=" + districts +
                '}';
    }

    /**
     * 重点：重写此方法，返回 WheelView 显示的文字
     * @return
     */
    @Override
    public String getWheelText() {
        return name == null ? "" : name;
    }
}
```
注意，我的 CityEntity 中多实现了一个 IWheelEntity 接口，这个接口是在 WheelView 库中定义好的，实现之后在 getWheelText() 方法返回你想在 WheelView 中展示的字段就大功告成了。

MainActivity WheelView 相关代码：
```java 
    WheelView<CityEntity> cityWv=findViewById(R.id.wv_city);
    //解析城市列表
    List<CityEntity> cityData= ParseHelper.parseTwoLevelCityList(this);
    cityWv.setData(cityData);
```
然后，效果图是这样的~

![WheelView_City](https://github.com/zyyoona7/WheelPicker/blob/master/perview/wheel_view_city.gif)

> 冷知识：其实不实现 IWheelEntity 也是可以的，偷偷给你们看下源码：
```java
    //WheelView.java
    /**
     * 获取item text
     *
     * @param item item数据
     * @return 文本内容
     */
    protected String getDataText(T item) {
        if (item == null) {
            return "";
        } else if (item instanceof IWheelEntity) {
            return ((IWheelEntity) item).getWheelText();
        } else if (item instanceof Integer) {
            //如果为整形则最少保留两位数.
            return isIntegerNeedFormat ? String.format(Locale.getDefault(), mIntegerFormat, (Integer) item)
                    : String.valueOf(item);
        } else if (item instanceof String) {
            return (String) item;
        }
        return item.toString();
    }
```
如果条件都不满足的话会默认执行 toString() 方法，所以理论上也可以在实体的 toString() 方法返回你想展示的字段，但是**不推荐**，毕竟 toString() 方法以我个人的习惯都是输出 CityEntity 那种的信息~你也可能输出别的信息。

#### PickerView Usage

```groovy
    implementation 'com.github.zyyoona7:pickerview:1.0.5'
```

#### DatePickerView Usage

在布局文件中：
```xml
    <com.zyyoona7.picker.DatePickerView
        android:id="@+id/dpv_default"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tv_datePickerView" />
```

代码中：
```java
    DatePickerView defaultDpv = findViewById(R.id.dpv_default);
    defaultDpv.setTextSize(24, true);
    defaultDpv.setLabelTextSize(20);
    
    //选中回调
    defaultDpv.setOnDateSelectedListener(new DatePickerView.OnDateSelectedListener() {
        @Override
        public void onDateSelected(DatePickerView datePickerView, int year, int month, int day, @Nullable Date date) {
            Toast.makeText(Main3Activity.this, "选中的日期：" + year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
        }
    });
```
> 问：我想要每一项都加入年字怎么写？

> 答：简单。

**定制格式**

我们需要先把自带显示年、月、日的 TextView 隐藏，然后设置格式化：

```java
    //隐藏年月日
    customDpv3.setShowLabel(false);    

    //获取年月日 WheelView
    YearWheelView yearWv3 = customDpv3.getYearWv();
    MonthWheelView monthWv3 = customDpv3.getMonthWv();
    DayWheelView dayWv3 = customDpv3.getDayWv();
    //注意：setIntegerNeedFormat(String integerFormat)方法 integerFormat 中必须包含并且只能包含一个格式说明符（format specifier）
    //更多请查看该方法参数说明
    yearWv3.setIntegerNeedFormat("%d年");
    monthWv3.setIntegerNeedFormat("%d月");
    dayWv3.setIntegerNeedFormat("%02d日");
```

没错就是这么简单，而且回调内容依旧不变~更多操作请查看 [Main3Activity](https://github.com/zyyoona7/WheelPicker/blob/master/app/src/main/java/com/zyyoona7/demo/Main3Activity.java)

#### OptionsPickerView

布局文件中：

```xml
    <com.zyyoona7.picker.OptionsPickerView
        android:id="@+id/opv_three_linkage"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/opv_two_linkage"
        android:background="#DCDCDC"/>
```

代码中：

```java
    final OptionsPickerView<CityEntity> threeLinkageOpv = findViewById(R.id.opv_three_linkage);
    //设置数据
    threeLinkageOpv.setLinkageData(p3List, c3List, d3List);
    //定制样式
    threeLinkageOpv.setVisibleItems(7);
    threeLinkageOpv.setResetSelectedPosition(true);
    threeLinkageOpv.setDrawSelectedRect(true);
    threeLinkageOpv.setSelectedRectColor(Color.parseColor("#D3D3D3"));
    threeLinkageOpv.setNormalItemTextColor(Color.parseColor("#808080"));
    threeLinkageOpv.setTextSize(22f,true);
    threeLinkageOpv.setSoundEffect(true);
    threeLinkageOpv.setSoundEffectResource(R.raw.button_choose);

    //设置选中回调
    threeLinkageOpv.setOnOptionsSelectedListener(new OptionsPickerView.OnOptionsSelectedListener<CityEntity>() {
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
```

更多请查看 [Main4Activity](https://github.com/zyyoona7/WheelPicker/blob/master/app/src/main/java/com/zyyoona7/demo/Main4Activity.java)

### 更新日志（Update Logs）

#### WheelView Update Logs
- **2018/10/10 发布 1.0.4 版本**
    - 在编写布局时可以实时预览
    
- **2018/08/29 发布 1.0.2 版本**
    - 修复 setSelectedItemPosition() 方法没有执行 onWheelSelected() 问题

- **2018/08/23 发布 1.0.1 版本**
    - 规范命名，将方法名和属性名保持一致命名
    - 增加绘制选中区域，设置选中区域颜色
    - 增加 getItemData(int position) 方法：获取指定 position 的数据
    - 增加 getSelectedItemData() 方法：获取当前选中的item数据
    - 增加 setIntegerNeedFormat(String integerFormat) 方法：同时设置 isIntegerNeedFormat=true 和 mIntegerFormat=integerFormat 两个属性
    - 增加 setResetSelectedPosition(boolean isResetSelectedPosition) 方法：设置当数据变化时，是否重置选中下标到第一个
    - 修改 mCurrentItemPosition 为 mSelectedItemPosition，并同时修改了对应的 getter/setter 方法及属性名
    - 修改 mSelectedItemColor 为 mSelectedItemTextColor，并同时修改了对应的 getter/setter 方法名
    - 修改 setSelectedItemPosition(int position) 默认不开启平滑滚动
    - 修复单次滑动 onItemSelected() 可能执行两次的问题
    - 修复 setSelectedItemPosition() 不开启平滑滚动时，未保存当前选中下标
    - 修复更新数据时，mSelectedItemPosition 越界的情况
    - 修复播放音效代码写错位置
    - 优化滚动监听和选中监听回调次数，回调更加精准，减少不必要的重绘
    - 优化滚动监听执行位置，onDraw() 方法不再处理任何回调

- **2018/08/20 发布 1.0.0 版本**
    - 泛型设置数据类型
    - 滚动音效，3D 效果等
    - 增加自动调整字体大小以使得长文字显示完全（需手动开启）
    - 丰富的监听器
    
#### PickerView Update Logs
- **18/11/17 发布 1.0.5 版本**
    - 修复 YearWheelView 中设置年份范围时不包括结束年的问题
    
- **18/10/10 发布 1.0.4 版本**
    - 同步 wheelview 版本，pickerview 终于可以正常导入，只有 1.0.4 版本可用
    
- ~~**18/09/14 发布 1.0.2 版本**~~
    -  ~~修复 pickerview 引入失败问题~~
    
- ~~**18/08/29 发布 1.0.1 版本**~~
    - ~~同步 WheelView 版本~~
   
- ~~**2018/08/24 发布 1.0.0 版本**~~
    - ~~YearWheelView、MonthWheelView、DayWheelView 封装~~
    - ~~日期选择器、选项选择器~~

### 感谢（Thanks）
[**WheelPicker**](https://github.com/AigeStudio/WheelPicker)<br>
[**Android-PickerView**](https://github.com/Bigkoo/Android-PickerView)<br>
[**WheelView**](https://github.com/CNCoderX/WheelView)<br>
[**WheelView-3d**](https://github.com/youxiaochen/WheelView-3d)<br>
[**DatePicker**](https://github.com/chenglei1986/DatePicker)

### LICENSE
```
Copyright 2018 zyyoona7

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
