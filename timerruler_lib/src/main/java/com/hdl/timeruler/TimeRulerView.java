package com.hdl.timeruler;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TextureView;

import com.hdl.timeruler.utils.CUtils;
import com.hdl.timeruler.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 时间刻度尺
 * Created by HDL on 2017/8/21.
 */

public class TimeRulerView extends TextureView implements TextureView.SurfaceTextureListener, ScaleScroller.ScrollingListener {
    /**
     * 滑动辅助类
     */
    private ScaleScroller mScroller;
    /**
     * 当前时间秒数【正中间】
     */
    private float currentSecond = 0;
    /**
     * 刻度配置
     */
    private Paint rulerPaint = new Paint();//刻度画笔
    private int rulerColor = 0xffb5b5b5;//刻度的颜色
    private int rulerWidthSamll = CUtils.dip2px(1);//小刻度的宽度
    private int rulerHeightSamll = CUtils.dip2px(10);//小刻度的高度
    private int rulerSpace = CUtils.dip2px(12);//刻度间的间隔

    private int rulerWidthBig = CUtils.dip2px(1);//大刻度的宽度
    private int rulerHeightBig = CUtils.dip2px(20);//大刻度的高度
    /**
     * 上下两条线
     */
    private Paint upAndDownLinePaint = new Paint();//刻度画笔
    private int upAndDownLineWidth = CUtils.dip2px(2);//上下两条线的宽度
    private int upAndDownLineColor = rulerColor;

    /**
     * 文本画笔
     */
    private TextPaint keyTickTextPaint = new TextPaint();
    private int textColor = 0xff444242;//文本颜色
    private int textSize = CUtils.dip2px(12);//文本大小
    /**
     * 中轴线画笔
     */
    private Paint centerLinePaint = new Paint();
    private int centerLineColor = 0xff6e9fff;//中轴线画笔
    private int centerLineWidth = CUtils.dip2px(2);
    /**
     * 视频区域画笔
     */
    private Paint vedioAreaPaint = new Paint();
    private int vedioBg = 0x336e9fff;//视频背景颜色
    private RectF vedioAreaRect = new RectF();
    /**
     * 背景颜色
     */
    private int bgColor = Color.WHITE;
    /**
     * view的高度
     */
    private int view_height = CUtils.dip2px(166);//view的高度
    /**
     * 是否自动移动时间轴
     */
    private boolean isMoving = true;
    /**
     * 当前日期的开始时间毫秒值
     */
    private long currentDateStartTimeMillis = DateUtils.getTodayStart(System.currentTimeMillis());
    /**
     * 视频时间段集合
     */
    private List<TimeSlot> vedioTimeSlot = new ArrayList<>();


    public TimeRulerView(Context context) {
        this(context, null);
    }

    public TimeRulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeRulerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new ScaleScroller(getContext(), this);
        setSurfaceTextureListener(this);
        initPaint();
        moveTimer();//开启移动定时器
    }

    /**
     * 设置view高度
     */
    public void setViewHeightForDp(int view_height) {
        this.view_height = CUtils.dip2px(view_height);
        refreshCanvas();
    }

    /**
     * 设置当前时间的毫秒值
     *
     * @param currentTimeMillis
     */
    public void setCurrentTimeMillis(long currentTimeMillis) {
        currentDateStartTimeMillis = DateUtils.getTodayStart(currentTimeMillis);
//        currentSecond = (currentTimeMillis - currentTimeMillis) / 1000f;
        //设置最左边的时间为当前时间减view宽度一半所占的时间--->这个时间所在的像素值就是
        lastPix = -((currentTimeMillis - currentDateStartTimeMillis) / 1000f - getWidth() / 2f * pixSecond) / pixSecond;
        refreshCanvas();
    }

    /**
     * 获取当前毫秒值
     *
     * @return
     */
    public long getCurrentTimeMillis() {
        return currentDateStartTimeMillis + (long) currentSecond * 1000L;
    }

    /**
     * 移动定时器
     */
    private void moveTimer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (isMoving) {
                    //(rulerWidthSamll + rulerSpace) / 60   表示1秒钟移动多少像素
                    lastPix -= (rulerWidthSamll + rulerSpace) / 60.0;
                    refreshCanvas();
                }
            }
        }, 0, 1000);
    }

    /**
     * 获取是否正在移动
     *
     * @return
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * 设置是否自动移动
     *
     * @param moving
     */
    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        rulerPaint.setAntiAlias(true);
        rulerPaint.setColor(rulerColor);
        rulerPaint.setStrokeWidth(rulerWidthSamll);

        keyTickTextPaint.setAntiAlias(true);
        keyTickTextPaint.setColor(textColor);
        keyTickTextPaint.setTextSize(textSize);

        centerLinePaint.setAntiAlias(true);
        centerLinePaint.setStrokeWidth(centerLineWidth);
        centerLinePaint.setColor(centerLineColor);

        vedioAreaPaint.setAntiAlias(true);
        vedioAreaPaint.setColor(vedioBg);

        upAndDownLinePaint.setAntiAlias(true);
        upAndDownLinePaint.setColor(upAndDownLineColor);
        upAndDownLinePaint.setStrokeWidth(upAndDownLineWidth);
    }

    //刷新视图
    private void refreshCanvas() {
        Canvas canvas = lockCanvas();
        if (canvas != null) {
            canvas.drawColor(bgColor);
            drawUpAndDownLine(canvas);
            drawTextAndRuler(canvas);//画文本和刻度
            drawRecodeArea(canvas);//画视频选择区域
            drawCenterLine(canvas);//画中间标线
        }
        unlockCanvasAndPost(canvas);
    }

    /**
     * 画视频区域
     *
     * @param canvas
     */
    private void drawRecodeArea(Canvas canvas) {
//        ELog.e(vedioTimeSlot);
        for (TimeSlot timeSlot : vedioTimeSlot) {
            //判断是否在可见范围内
            if (timeSlot.getEndTime() > getScreenLeftTimeSeconde() && timeSlot.getStartTime() < getScreenRightTimeSeconde()) {
                //画可见区域内的视频
                float startX = getRightXByTimeSeconde(timeSlot.getStartTime()) + lastPix;
                startX = startX < 0 ? 0 : startX;//左边超出屏幕（<0）的就不画
                float endX = getRightXByTimeSeconde(timeSlot.getEndTime()) + lastPix;
                endX = endX > getWidth() ? getWidth() : endX;//右边超出屏幕（>getWidth）的就不画
                vedioAreaRect.set(startX, 0, endX, view_height - textSize);
                canvas.drawRect(vedioAreaRect, vedioAreaPaint);
            }
        }
    }

    /**
     * 获取视频时间段
     *
     * @return
     */
    public List<TimeSlot> getVedioTimeSlot() {
        return vedioTimeSlot;
    }

    /**
     * 设置视频时间段
     *
     * @param vedioTimeSlot
     */
    public void setVedioTimeSlot(List<TimeSlot> vedioTimeSlot) {
        this.vedioTimeSlot.clear();
        this.vedioTimeSlot.addAll(vedioTimeSlot);
        refreshCanvas();
    }

    /**
     * 通过时间获取开始x坐标
     *
     * @param timeSeconde
     * @return
     */
    private float getRightXByTimeSeconde(float timeSeconde) {
        return timeSeconde / pixSecond;
    }

    /**
     * 获取屏幕最左边时间.
     * 用来判断是否需要画视频区域(不可见的就不画)
     *
     * @return
     */
    private float getScreenLeftTimeSeconde() {
        return getCurrentSecond() - getWidth() / 2 * pixSecond;
    }

    /**
     * 获取屏幕最右边时间
     * 用来判断是否需要画视频区域(不可见的就不画)
     *
     * @return
     */
    private float getScreenRightTimeSeconde() {
        return getCurrentSecond() + getWidth() / 2 * pixSecond;
    }

    /**
     * 画中间线
     *
     * @param canvas
     */
    private void drawCenterLine(Canvas canvas) {
        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, view_height - textSize, centerLinePaint);
    }

    /**
     * 设置当前秒数
     *
     * @param currentSecond
     */
    public void setCurrentSecond(int currentSecond) {
        this.currentSecond = currentSecond;
        refreshCanvas();
    }

    /**
     * 获取当前时间的秒数
     *
     * @return
     */
    public float getCurrentSecond() {
        return currentSecond;
    }

    /**
     * 每秒有多少个像素
     */
    private float pixSecond = 0;//每秒有多少个像素，s/px

    /**
     * 画文本和刻度
     *
     * @param canvas
     */
    private void drawTextAndRuler(Canvas canvas) {
        rulerPaint.setStrokeWidth(rulerWidthBig);
        int viewWidth = getWidth();
        int itemWidth = rulerWidthSamll + rulerSpace;//单个view的宽度
        pixSecond = 60f / itemWidth;//itemWidth表示一分钟，
        int count = viewWidth / itemWidth;
        if (lastPix < 0) {//<0表示往左边移动-->右滑
            count += -lastPix / 10;//需要加上移动的距离
        }
        //从屏幕左边开始画刻度和文本，从上一天的23:30开始
        for (int index = -30; index < count; index++) {
            float rightX = index * itemWidth + lastPix;//右边方向x坐标
            if (index == 0) {//根据最左边的时刻算最中间的时刻，左边时刻(rightX*每秒多少像素)+中间时刻（view的宽度/2*每秒多少像素）
                if (rightX < 0) {//15分钟之后的移动
                    currentSecond = viewWidth * pixSecond / 2f + Math.abs(rightX * pixSecond);
                } else {//15分钟之前的移动
                    currentSecond = viewWidth * pixSecond / 2f - rightX * pixSecond;
                }
                lastConfigChangedTime = getCurrentTimeMillis();//记录切换前的时间
            }
            if (index % 10 == 0) {//大刻度
                //画上边刻度
                canvas.drawLine(rightX, 0, rightX, rulerHeightBig, rulerPaint);
                //画下边刻度
                canvas.drawLine(rightX, view_height - textSize, rightX, view_height - rulerHeightBig - textSize, rulerPaint);
                //画文本
                draText(canvas, index * 60, rightX);
            } else {//小刻度
                //画上面小刻度
                canvas.drawLine(rightX, 0, rightX, rulerHeightSamll, rulerPaint);
                //画下面小刻度
                canvas.drawLine(rightX, view_height - textSize, rightX, view_height - rulerHeightSamll - textSize, rulerPaint);
            }
        }
    }

    /**
     * 画上下两条线
     *
     * @param canvas
     */
    private void drawUpAndDownLine(Canvas canvas) {
        int viewWidth = getWidth();
        //画上下两条线
        canvas.drawLine(0, upAndDownLineWidth / 2, viewWidth, rulerWidthSamll / 2, upAndDownLinePaint);
        canvas.drawLine(0, view_height - textSize, viewWidth, view_height - textSize, upAndDownLinePaint);
    }

    /**
     * 绘制文本所需参数，提为全局是防止多次创建无用对象
     */
    private String keyText = "";//文本
    private float keyTextX = 0;//文本的x轴坐标
    private float keyTextWidth = 0;//文本的宽度

    /**
     * 画时间文本
     *
     * @param canvas
     * @param time   当前时间
     * @param x      所画时间x轴坐标
     */
    public void draText(Canvas canvas, int time, float x) {
        if (time < 0) {//上一天
            keyText = DateUtils.getTimeByCurrentSecond(24 * 60 * 60 + time);
        } else {
            keyText = DateUtils.getTimeByCurrentSecond(time);
        }
        keyTextWidth = keyTickTextPaint.measureText(keyText);
        keyTextX = x - keyTextWidth / 2;
        canvas.drawText(keyText, keyTextX, view_height, keyTickTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScroller.onTouchEvent(event);
        return true;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        refreshCanvas();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    /**
     * 移动偏移量
     */
    private float lastPix = 0;//移动/滑动的距离，绘制的时候需要根据这个值来确定位置

    /**
     * 滑动时
     *
     * @param distance
     */
    @Override
    public void onScroll(int distance) {
//        ELog.e("distance" + distance);
        lastPix += distance;
        refreshCanvas();
    }

    @Override
    public void onStarted() {
    }

    /**
     * 滑动结束
     */
    @Override
    public void onFinished() {
//        ELog.e("滑动结束--------");
    }

    /**
     * 缩放时
     *
     * @param mScale
     * @param time
     */
    @Override
    public void onZoom(float mScale, double time) {
    }

    private long lastConfigChangedTime;//最后横竖屏切换时的时间--->让横竖屏时间一致

    /**
     * 横竖屏切换的时候，重绘一下(也不能马上就重绘，需要有一个延迟)
     *
     * @param newConfig
     */
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setCurrentTimeMillis(lastConfigChangedTime);
            }
        }, 100);
    }
}
