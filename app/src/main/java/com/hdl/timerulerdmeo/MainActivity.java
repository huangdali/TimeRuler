package com.hdl.timerulerdmeo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hdl.elog.ELog;
import com.hdl.timeruler.TimeRuler;

public class MainActivity extends AppCompatActivity {

    private TimeRuler tRuler;
    private int currentSecond = 10 * 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tRuler = (TimeRuler) findViewById(R.id.tr_line);
    }

    public void toTestPage(View view) {
        startActivity(new Intent(this, Main2Activity.class));
    }

    public void toAdd(View view) {
        currentSecond +=10*60;//加一分钟
        ELog.e("当前时间加一分钟" + currentSecond);
        tRuler.setCurrentSecond(currentSecond);
    }
}
