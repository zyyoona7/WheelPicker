# WheelPicker

### WheelView 简介（Introduction）

自定义实现 WheelView，滑动流畅、功能齐全、用法简单、高度自定义。

### WheelView 特性（Features）

- 如丝般顺滑的滚动效果，无论快速滚动还是缓慢滚动
- 灵活数据设置，通过泛型设置数据类型，灵活、安全
- 支持类似 iOS 的滚动变化音效
- 支持嵌套滚动、循环滚动
- 丰富的滑动监听，支持选中监听、滚动状态改变监听等
- 支持类似 iOS 的 3D 效果
- 3D 效果下，支持圆弧偏移效果，使其看起来更加立体
- 两种分割线类型可以设置，还有其他分割线骚操作
- 支持设置显示条目、设置字体大小、设置字体、设置行间距等常规操作
- 更多自定义操作尽在其中

### WheelView 效果图（Preview）

![WheelView 1](https://github.com/zyyoona7/WheelPicker/blob/master/perview/wheel_view_1.gif)
①  ②
![WheelView 2](https://github.com/zyyoona7/WheelPicker/blob/master/perview/wheel_view_2.gif)
<br><br>
![WheelView 3](https://github.com/zyyoona7/WheelPicker/blob/master/perview/wheel_view_3.gif)
③  ④
![WheelView 4](https://github.com/zyyoona7/WheelPicker/blob/master/perview/wheel_view_4.gif)

### WheelView 使用（Usage）
#### 1.依赖（dependency）
//TODO publish jitpack

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

详细使用请阅读 [WIKI](https://github.com/zyyoona7/WheelPicker/wiki)

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
