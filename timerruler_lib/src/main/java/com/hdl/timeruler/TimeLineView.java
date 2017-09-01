package com.hdl.timeruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.hdl.elog.ELog;

import static com.hdl.timeruler.TimeRuler.dip2px;

/**
 * 画时间刻度
 * Created by HDL on 2017/9/1.
 */

public class TimeLineView extends View {
    /**
     * 刻度配置
     */
    private Paint rulerPoint = new Paint();//刻度画笔
    private int rulerColor = 0xff444242;//刻度的颜色
    private int rulerWidthSamll = dip2px(1);//刻度的宽度
    private int rulerHeightSamll = dip2px(10);//小刻度的高度
    private int rulerSpace = dip2px(12);//刻度间的间隔

    private int rulerWidthBig = dip2px(1);//刻度的宽度
    private int rulerHeightBig = dip2px(20);//大刻度的高度
    /**
     * 文本画笔
     */
    private TextPaint keyTickTextPaint = new TextPaint();
    private int textColor = 0xff444242;//文本颜色
    private int textSize = dip2px(12);//文本大小
    /**
     * 中轴线画笔
     */
    private Paint centerLinePaint = new Paint();
    private int centerLineColor = 0xff6e9fff;//中轴线画笔
    private int centerLineWidth = dip2px(2);
    /**
     * 整个view移动的距离
     */
    private static float translateX = 0;
    /**
     * 背景颜色
     */
    private int bgColor = Color.WHITE;

    private int view_height = dip2px(166);//view的高度
    private int view_width = dip2px(166);//view的宽度

    public TimeLineView(Context context) {
        this(context, null);
    }

    public TimeLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeLineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaint();
    }

    private void initPaint() {
        rulerPoint.setAntiAlias(true);
        rulerPoint.setColor(rulerColor);
        rulerPoint.setStrokeWidth(rulerWidthSamll);

        keyTickTextPaint.setAntiAlias(true);
        keyTickTextPaint.setColor(textColor);
        keyTickTextPaint.setTextSize(textSize);

        centerLinePaint.setAntiAlias(true);
        centerLinePaint.setStrokeWidth(centerLineWidth);
        centerLinePaint.setColor(centerLineColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        view_width = getWidth();
        ELog.e("view_width=" + view_width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTextAndRuler(canvas);//画文本和刻度
    }

    /**
     * 画时间刻度
     *
     * @param canvas
     */
    private void drawTextAndRuler(Canvas canvas) {
        for (int i = 0; i < totalRulerNum; i++) {//总的需要画的时间刻度数

        }
    }

    private int centerTimeNum = 20;//中间时间刻度
    private int currentAccuracy = 1;//当前时间进度，1分钟
    private int totalRulerNum = 40 * currentAccuracy;//总的刻度数
}

