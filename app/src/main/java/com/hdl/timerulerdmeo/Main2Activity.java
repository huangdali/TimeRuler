package com.hdl.timerulerdmeo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

public class Main2Activity extends AppCompatActivity {

    private ImageView ivTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ivTest = (ImageView) findViewById(R.id.iv_test);
    }

    private float lastX, lastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getRawX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = event.getRawX() - lastX;
                ivTest.setTranslationX(event.getX());
                ivTest.setTranslationY(event.getY());
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }
}
