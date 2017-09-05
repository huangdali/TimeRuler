package com.hdl.timerulerdmeo;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hdl.elog.ELog;
import com.hdl.timeruler.OnSelectedTimeListener;
import com.hdl.timeruler.TimeRulerView;
import com.hdl.timeruler.TimeSlot;
import com.hdl.timeruler.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TimeRulerView tRuler;
    private TextView tvTime;
    private long date;
    private TextView tvProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tRuler = (TimeRulerView) findViewById(R.id.tr_line);
        tRuler.setMoving(false);
//        tRuler.setCurrentTimeMillis(System.currentTimeMillis());
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        tRuler.setOnSelectedTimeListener(new OnSelectedTimeListener() {
            @Override
            public void onDragging(long startTime, long endTime) {
                tvProgress.setText(DateUtils.getDateByCurrentTiem(startTime) + " " + DateUtils.getTime(startTime) + "-" + DateUtils.getDateByCurrentTiem(endTime) + " " + DateUtils.getTime(endTime));
            }

            @Override
            public void onMaxTime() {
                Toast.makeText(MainActivity.this, "超过最大时间", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMinTime() {
                Toast.makeText(MainActivity.this, "超过最小时间", Toast.LENGTH_SHORT).show();
            }
        });
        tvTime = (TextView) findViewById(R.id.tv_time);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        float currentSecond = tRuler.getCurrentSecond();
                        date = tRuler.getCurrentTimeMillis();
                        tvTime.setText(DateUtils.getDateByCurrentTiem(date) + " " + DateUtils.getTime(date));
                    }
                });
            }
        }, 0, 1000);
//        tRuler.setCurrentTimeMillis(System.currentTimeMillis());
    }

    public void toTestPage(View view) {
        startActivity(new Intent(this, Main2Activity.class));
    }

    public void toAdd(View view) {
//        tRuler.setCurrentTimeMillis(System.currentTimeMillis());
        tRuler.setCurrentTimeMillis(DateUtils.getTodayStart(System.currentTimeMillis()) + 8 * 60 * 60 * 1000);
    }

    boolean isMove = true;

    public void toMoveOrPause(View view) {
        isMove = !isMove;
        tRuler.setMoving(isMove);
    }

    /**
     * 显示视频区域
     *
     * @param view
     */
    public void showVedioArea(View view) {
        List<TimeSlot> times = new ArrayList<>();
        times.add(new TimeSlot(DateUtils.getTodayStart(System.currentTimeMillis()) + 60 * 60 * 1000, DateUtils.getTodayStart(System.currentTimeMillis()) + 120 * 60 * 1000));
        times.add(new TimeSlot(DateUtils.getTodayStart(System.currentTimeMillis()) + 3 * 60 * 60 * 1000, DateUtils.getTodayStart(System.currentTimeMillis()) + 4 * 60 * 60 * 1000));
        tRuler.setVedioTimeSlot(times);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            tRuler.setViewHeightForDp(166);
        } else {
            tRuler.setViewHeightForDp(90);
        }
    }

    boolean isSelected = true;

    public void toSelected(View view) {
        tRuler.setSelectTimeArea(isSelected);
        isSelected = !isSelected;
    }

    public void getSelected(View view) {
        ELog.e("tRuler.getSelectStartTime()=" + DateUtils.getDateByCurrentTiem(tRuler.getSelectStartTime()) + "  " + DateUtils.getTime(tRuler.getSelectStartTime()));
        tvProgress.setText(DateUtils.getTime(tRuler.getSelectStartTime()) + "-" + DateUtils.getTime(tRuler.getSelectEndTime()));
    }
}
