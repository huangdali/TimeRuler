package com.hdl.timerulerdmeo;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.hdl.elog.ELog;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tRuler = (TimeRulerView) findViewById(R.id.tr_line);
        tvTime = (TextView) findViewById(R.id.tv_time);
        ELog.e("创建了");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         date = tRuler.getCurrentTimeMillis();
                        tvTime.setText(DateUtils.getDateByCurrentTiem(date) + " " + DateUtils.getTime(date));
                    }
                });
            }
        }, 0, 1000);
    }

    public void toTestPage(View view) {
        startActivity(new Intent(this, Main2Activity.class));
    }

    public void toAdd(View view) {
        tRuler.setCurrentTimeMillis(DateUtils.getTodayStart(System.currentTimeMillis())+(long) 1.5*60*60*1000);
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
        times.add(new TimeSlot(DateUtils.getTodayStart(System.currentTimeMillis()) + 3*60 * 60 * 1000, DateUtils.getTodayStart(System.currentTimeMillis()) + 4*60 * 60 * 1000));
        tRuler.setVedioTimeSlot(times);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation== Configuration.ORIENTATION_PORTRAIT) {
            tRuler.setViewHeightForDp(166);
        }else{
            tRuler.setViewHeightForDp(90);
        }
    }
}
