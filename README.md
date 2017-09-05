# TimeRuler

时间轴、时间刻度尺

- 已适配横竖屏
- 自动移动（自由决定开启与关闭移动）
- 时间轴中选择时间
- 实时设置当天时间
- 显示有效视频时间
- 超时（超过00:00:00,、23:59:59）自动处理
- 带拖动开始、结束、自动移动、超时回调
- 带时间选择回调


## 效果图

![](https://github.com/huangdali/TimeRuler/blob/master/timerulers.gif)

## 新增时间选择

通过setSelectTimeArea(bool)就可以设置是否显示时间选择

![](https://github.com/huangdali/TimeRuler/blob/master/new.png)

## 使用

### 导入
app.build中使用

```java
    compile 'com.jwkj:TimeLineView:v1.1.2'
```

### 混淆配置

```java
#timeruler
-keep class com.hdl.timeruler.**{*;}
-dontwarn com.hdl.timeruler.**
```


### 开启硬件加速

所在activity需要开启硬件加速(建议配置横竖屏不重新走一遍生命周期)

 ```java
    <activity
       ...
       android:configChanges="orientation|keyboardHidden|screenSize"
       android:hardwareAccelerated="true">
       ...
    </activity>
 ```

### 布局

```java
 <com.hdl.timeruler.TimeRulerView
            android:id="@+id/tr_line"
            android:layout_width="match_parent"
            android:layout_height="166dp" />
```

### 设置当前时间

```java
tRuler.setCurrentTimeMillis(设置中心线的时间)
```

### 初始化视频时间段

```java
        List<TimeSlot> times = new ArrayList<>();
        times.add(new TimeSlot(DateUtils.getTodayStart(System.currentTimeMillis()) + 60 * 60 * 1000, DateUtils.getTodayStart(System.currentTimeMillis()) + 120 * 60 * 1000));
        times.add(new TimeSlot(DateUtils.getTodayStart(System.currentTimeMillis()) + 3*60 * 60 * 1000, DateUtils.getTodayStart(System.currentTimeMillis()) + 4*60 * 60 * 1000));
        tRuler.setVedioTimeSlot(times);
```

### 是否自动移动
```java
    tRuler.openMove();//打开移动
    tRuler.closeMove();//关闭移动
```

## 版本记录

v1.1.2( [2017.09.05]() )

- 【优化】 onBarMoving在主线程中执行

v1.0.8( [2017.09.05]() )

- 【优化】 删除无用依赖和无用代码

v1.0.7( [2017.09.05]() )

- 【优化】 使用openMove()、closeMove()代替setMoving(bool)

v1.0.6( [2017.09.05]() )

- 【新增】超过今天开始时间（00：00:00）、今天结束时间（23:59:59）回调，并自动回到开始/结束时间

v1.0.5( [2017.09.05]() )

- 【新增】新增时间轴上选择时间

v1.0.4( [2017.09.04]() )

- 【修复】往小于15分钟拉取时时间倒跑问题

> 更早版本未记录