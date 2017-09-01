package com.hdl.timeruler;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.WindowManager;

import com.hdl.elog.ELog;
import com.hdl.timeruler.utils.TimeUtils;


/**
 * 时间刻度尺
 * Created by HDL on 2017/8/21.
 */

public class TimeRuler extends TextureView implements TextureView.SurfaceTextureListener, ScaleScroller.ScrollingListener {
    private Context mContext;
    private ScaleScroller mScroller;
    /**
     * 屏幕宽高
     */
    private int screenWidth, screenHeight;
    /**
     * 总秒数，3天
     */
    private int totalSecond = 3 * 24 * 60 * 60;
    /**
     * 当前秒数
     */
    private int currentSecond = 0;
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
     * 视频区域画笔
     */
    private Paint vedioAreaPaint = new Paint();
    private int vedioBg = 0x336e9fff;
    private RectF vedioAreaRect = new RectF();


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

    public TimeRuler(Context context) {
        this(context, null);
    }

    public TimeRuler(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeRuler(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        mScroller = new ScaleScroller(getContext(), this);
        setSurfaceTextureListener(this);
        getWidthAndHeightFromScreen();
        initPaint();
    }

    public static int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 获取屏幕宽高
     */
    private void getWidthAndHeightFromScreen() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        android.view.Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
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

        vedioAreaPaint.setAntiAlias(true);
        vedioAreaPaint.setColor(vedioBg);
    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        canvas.translate(translateX, 0);
//        drawTextAndRuler(canvas);//画文本和刻度
//        drawCenterLine(canvas);
//    }

    //刷新视图
    private void refreshCanvas() {
        Canvas canvas = lockCanvas();
        if (canvas != null) {
//            canvas.translate(distance, 0);
            canvas.drawColor(bgColor);
            drawTextAndRuler(canvas);//画文本和刻度
            drawRecodeArea(canvas);
            drawCenterLine(canvas);
        }
        unlockCanvasAndPost(canvas);
    }

    /**
     * 画视频区域
     *
     * @param canvas
     */
    private void drawRecodeArea(Canvas canvas) {

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
     * 画文本和刻度
     *
     * @param canvas
     */
    private void drawTextAndRuler(Canvas canvas) {
        rulerPoint.setStrokeWidth(rulerWidthBig);
        int viewWidth = getWidth();
        canvas.drawLine(0, rulerWidthSamll / 2, viewWidth, rulerWidthSamll / 2, rulerPoint);
        canvas.drawLine(0, view_height - textSize, viewWidth, view_height - textSize, rulerPoint);
        int itemWidth = rulerWidthSamll + rulerSpace;
        int count = viewWidth / itemWidth;
        for (int i = 0; i < 24 * 60; i++) {
            float rightX = i * itemWidth + lastPix;//右边方向x坐标
            if ((currentSecond + i) % 10 == 0) {//大刻度
                //画上边刻度
                canvas.drawLine(rightX, 0, rightX, rulerHeightBig, rulerPoint);
                //画下边刻度
                canvas.drawLine(rightX, view_height - textSize, rightX, view_height - rulerHeightBig - textSize, rulerPoint);
                //画文本
                draText(canvas, currentSecond + i * 60, rightX);
            } else {//小刻度
                //画上面小刻度
                canvas.drawLine(rightX, 0, rightX, rulerHeightSamll, rulerPoint);
                //画下面小刻度
                canvas.drawLine(rightX, view_height - textSize, rightX, view_height - rulerHeightSamll - textSize, rulerPoint);
            }
            vedioAreaRect.set(itemWidth + lastPix, 0, itemWidth + lastPix + 100, view_height - textSize);
            canvas.drawRect(vedioAreaRect, vedioAreaPaint);
        }


//        for (int i = 0; i < count; i++) {
//            float rightX = i * itemWidth;//右边方向x坐标
//            drawRuler(canvas, currentSecond + i, rightX, i);
//        }
//        for (int i = 0; i < count ; i++) {
//            float rightX = viewWidth / 2 + i * itemWidth;//右边方向x坐标
//            float leftX = viewWidth / 2 - i * itemWidth;//左边方向x坐标
//            drawRuler(canvas, currentSecond + i, rightX, i);
//            drawRuler(canvas, currentSecond - i, leftX, i);
//        }
    }

    /**
     * 画刻度
     *
     * @param canvas
     * @param currentSecond
     * @param x
     * @param index
     */
    public void drawRuler(Canvas canvas, int currentSecond, float x, int index) {
        ELog.e("currentSecond=" + currentSecond);
        if ((currentSecond) % 10 == 0) {//大刻度
            ELog.e("大刻度");
            //画上边刻度
            canvas.drawLine(x, 0, x, rulerHeightBig, rulerPoint);
            //画下边刻度
            canvas.drawLine(x, view_height - textSize, x, view_height - rulerHeightBig - textSize, rulerPoint);
            //画文本
            draText(canvas, currentSecond + index * 60, x);
        } else {//小刻度
            ELog.e("小刻度");
            //画上面小刻度
            canvas.drawLine(x, 0, x, rulerHeightSamll, rulerPoint);
            //画下面小刻度
            canvas.drawLine(x, view_height - textSize, x, view_height - rulerHeightSamll - textSize, rulerPoint);
        }
    }

    //绘制文本所需参数，提为全局是防止多次创建无用对象
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
        keyText = TimeUtils.getTimeByCurrentSecond(time);
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

    private float lastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScroller.onTouchEvent(event);
        return true;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //重绘了
            ELog.e("重绘了");
            postInvalidate();
        }
    };

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        ELog.e("onSurfaceTextureAvailable", "width=" + width + " height=" + height);
        refreshCanvas();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        ELog.e("width=" + width + " height=" + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private int lastPix = 0;

    /**
     * 滑动时
     *
     * @param distance
     */
    @Override
    public void onScroll(int distance) {
        ELog.e("distance" + distance);
//        this.distance += distance;
        lastPix += distance;
//        if (distance > 0) {
//            currentSecond--;
//        } else {
//            currentSecond++;
//        }
        refreshCanvas();
    }

    private int distance = 0;

    @Override
    public void onStarted() {

    }

    /**
     * 滑动结束
     */
    @Override
    public void onFinished() {

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

    /**
     * 获取缩放前所表示的时间，为后续保证缩放时当前时间不变做准备
     *
     * @return
     */
    @Override
    public float getTime() {
        return 0;
    }

    /**
     * 设置当前显示位置为所需显示时间
     *
     * @param time 小数  单位：小时
     */
    @Override
    public void setTime(double time) {

    }
}
