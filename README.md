# TimeRuler

时间轴、时间刻度尺

## 效果图

![](https://github.com/huangdali/TimeRuler/blob/master/timerulers.gif)

## 新增时间选择

通过setSelectTimeArea(bool)就可以设置是否显示时间选择

![](https://github.com/huangdali/TimeRuler/blob/master/new.png)

## 使用

### 导入
app.build中使用

```java
    compile 'com.jwkj:TimeLineView:v1.0.6'
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
    tRuler.setMoving(true);//默认true
```

## 版本记录

v1.0.6( [2017.09.05]() )

- 【新增】超过今天开始时间（00：00:00）、今天结束时间（23:59:59）回调，并自动回到开始/结束时间

v1.0.5( [2017.09.05]() )

- 【新增】新增时间轴上选择时间

v1.0.4( [2017.09.04]() )

- 【修复】往小于15分钟拉取时时间倒跑问题

> 更早版本未记录