package com.hdl.timerulerdmeo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hdl.elog.ELog;
import com.hdl.timeruler.TimeRulerView;
import com.hdl.timeruler.TipView;
import com.hdl.timeruler.bean.OnBarMoveListener;
import com.hdl.timeruler.bean.OnSelectedTimeListener;
import com.hdl.timeruler.bean.TimeSlot;
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
    private RelativeLayout llP;
    private LinearLayout llH;
    private TipView tvTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
//        tRuler.setCurrentTimeMillis(System.currentTimeMillis());
//              tRuler.setCurrentTimeMillis(System.currentTimeMillis()-1*24*60*60*1000L);
    }

    private void initView() {
        tRuler = (TimeRulerView) findViewById(R.id.tr_line);
        tRuler.setCurrentTimeMillis(1500480000000L - 2 * 60 * 60 * 1000);
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        tvTime = (TextView) findViewById(R.id.tv_time);
        llP = (RelativeLayout) findViewById(R.id.ll_porental);
        llH = (LinearLayout) findViewById(R.id.ll_lanspace);
        tvTip = (TipView) findViewById(R.id.tv_tip);
    }

    private void setListener() {
        tRuler.setOnSelectedTimeListener(new OnSelectedTimeListener() {
            @Override
            public void onDragging(long startTime, long endTime) {
                tvProgress.setText(DateUtils.getTime(startTime) + "-" + DateUtils.getTime(endTime));
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
        tRuler.setOnBarMoveListener(new OnBarMoveListener() {
            @Override
            public void onDragBar(boolean isLeftDrag, long currentTime) {
//                ELog.e("拖动");
//                ELog.e("isLeftDrag(左边？)=" + isLeftDrag);
//                ELog.e("currentTime" + DateUtils.getDateTime(currentTime));
            }

            @Override
            public void onBarMoving(long currentTime) {
//                ELog.e("currentTime" + DateUtils.getDateTime(currentTime));
                currentTimeMillis = currentTime;
            }

            @Override
            public void onBarMoveFinish(long currentTime) {
                ELog.e("拖动完成" + DateUtils.getDateTime(currentTime));
                Toast.makeText(MainActivity.this, "拖动完成", Toast.LENGTH_SHORT).show();
            }

            /**
             * 移动超过开始时间
             */
            @Override
            public void onMoveExceedStartTime() {
                ELog.e("超过开始时间了");
            }

            /**
             * 移动超过结束时间
             */
            @Override
            public void onMoveExceedEndTime() {
                ELog.e("超过结束时间了");
            }

            /**
             * 超过最大缩放值
             */
            @Override
            public void onMaxScale() {

            }

            /**
             * 超过最小缩放值
             */
            @Override
            public void onMinScale() {

            }
        });
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
        List<TimeSlot> times = new ArrayList<>();
//        times.add(new TimeSlot(DateUtils.getTodayStart(System.currentTimeMillis()), DateUtils.getTodayStart(System.currentTimeMillis()) - 60 * 60 * 1000, DateUtils.getTodayStart(System.currentTimeMillis()) + 120 * 60 * 1000));
//        times.add(new TimeSlot(DateUtils.getTodayStart(System.currentTimeMillis()), DateUtils.getTodayStart(System.currentTimeMillis()) + 3 * 60 * 60 * 1000, DateUtils.getTodayStart(System.currentTimeMillis()) + 4 * 60 * 60 * 1000));
//        times.add(new TimeSlot(DateUtils.getTodayStart(System.currentTimeMillis()), DateUtils.getTodayStart(System.currentTimeMillis()) + 11 * 60 * 60 * 1000, DateUtils.getTodayStart(System.currentTimeMillis()) + 13 * 60 * 60 * 1000));
        times.add(new TimeSlot(DateUtils.getTodayStart(1500480000074L - 2 * 60 * 60 * 1000), 1500480000074L - 2 * 60 * 60 * 1000, 1500480000074L));
        Log.e("hdltag", "setListener(MainActivity.java:121):" + times);
        tRuler.setVedioTimeSlot(times);
    }

    public void toTestPage(View view) {
        startActivity(new Intent(this, Main2Activity.class));
    }

    public void toAdd(View view) {
        tRuler.setCurrentTimeMillis(System.currentTimeMillis());
//        tRuler.setCurrentTimeMillis(DateUtils.getTodayStart(System.currentTimeMillis()) + 23 * 60 * 60 * 1000);
//        tRuler.setCurrentTimeMillis(DateUtils.getTodayStart(System.currentTimeMillis()) + 20 * 60 * 1000);
    }

    boolean isMove = true;

    public void toMoveOrPause(View view) {
        isMove = !isMove;
        if (isMove) {
            tRuler.openMove();
        } else {
            tRuler.closeMove();
        }
    }

    /**
     * 显示视频区域
     *
     * @param view
     */
    public void showVedioArea(View view) {
        List<TimeSlot> times = new ArrayList<>();
        times.add(new TimeSlot(DateUtils.getTodayStart(System.currentTimeMillis()), DateUtils.getTodayStart(System.currentTimeMillis()) + 60 * 60 * 1000, DateUtils.getTodayStart(System.currentTimeMillis()) + 120 * 60 * 1000));
        times.add(new TimeSlot(DateUtils.getTodayStart(System.currentTimeMillis()), DateUtils.getTodayStart(System.currentTimeMillis()) + 3 * 60 * 60 * 1000, DateUtils.getTodayStart(System.currentTimeMillis()) + 4 * 60 * 60 * 1000));
        tRuler.setVedioTimeSlot(times);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            llH.removeAllViews();
            llH.setVisibility(View.GONE);
            llP.setVisibility(View.VISIBLE);
            llP.addView(tRuler);
        } else {
            llP.removeAllViews();
            llP.setVisibility(View.GONE);
            llH.setVisibility(View.VISIBLE);
            llH.addView(tRuler);
        }
        Log.e("hdltag", "onConfigurationChanged(MainActivity.java:175):" + currentTimeMillis);
        ELog.e("横竖屏了" + DateUtils.getDateTime(currentTimeMillis));
        tRuler.setCurrentTimeMillis(currentTimeMillis);
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

    public void onHalfScreen(View view) {
//        currentTimeMillis = tRuler.getCurrentTimeMillis();
        ELog.e(DateUtils.getDateTime(currentTimeMillis));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
    }

    private long currentTimeMillis;

    public void onExitHalfScreen(View view) {
//        currentTimeMillis = tRuler.getCurrentTimeMillis();
        ELog.e(DateUtils.getDateTime(currentTimeMillis));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
    }

    public void onShowOrHideRight(View view) {
        tvTip.setShowRightTip(true);
    }

    public void onShowOrHideLeft(View view) {
        tvTip.setShowLeftTip(true);
    }
}
