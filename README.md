# TimeRuler

时间轴、时间刻度尺

## 效果图

![](https://github.com/huangdali/TimeRuler/blob/master/timerulers.gif)

## 使用

### 导入
app.build中使用

```java
    compile 'com.jwkj:TimeLineView:v1.0.3'
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

