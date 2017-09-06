package com.hdl.timeruler;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;

import com.hdl.timeruler.bean.OnBarMoveListener;
import com.hdl.timeruler.bean.OnSelectedTimeListener;
import com.hdl.timeruler.bean.ScaleMode;
import com.hdl.timeruler.bean.TimeSlot;
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

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TimeRulerView extends TextureView implements TextureView.SurfaceTextureListener, ScaleScroller.ScrollingListener {
    /**
     * 滑动辅助类
     */
    private ScaleScroller mScroller;
    /**
     * 当前时间秒数（中轴线时间）
     */
    private float currentSecond = 0;
    /**
     * 刻度配置
     */
    private Paint smallRulerPaint = new Paint();//小刻度画笔
    private int rulerColor = 0xffb5b5b5;//刻度的颜色
    private int rulerWidthSamll = CUtils.dip2px(0.5f);//小刻度的宽度
    private int rulerHeightSamll = CUtils.dip2px(10);//小刻度的高度
    private static final int DEFAULT_RULER_SPACE = CUtils.dip2px(12);//设置item默认间隔
    private int rulerSpace = DEFAULT_RULER_SPACE;//刻度间的间隔
    private static final int MAX_SCALE = CUtils.dip2px(39);//最大缩放值
    private static final int MIN_SCALE = CUtils.dip2px(5);//最小缩放值

    private Paint largeRulerPaint = new Paint();//大刻度画笔
    private int rulerWidthBig = CUtils.dip2px(0.5f);//大刻度的宽度
    private int rulerHeightBig = CUtils.dip2px(20);//大刻度的高度
    /**
     * 上下两条线
     */
    private Paint upAndDownLinePaint = new Paint();//刻度画笔
    private int upAndDownLineWidth = CUtils.dip2px(1);//上下两条线的宽度
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
     * 选择时间配置
     */
    private Paint selectAreaPaint = new Paint();//选择时间边框
    private int selectTimeBorderColor = 0xfffabb64;//边框颜色
    private Paint vedioArea = new Paint();//已选时间
    private int selectTimeAreaColor = 0x33fabb64;//已选时间颜色
    private float selectTimeStrokeWidth = CUtils.dip2px(8);

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
     * 是否显示选择区域
     */
    private boolean isSelectTimeArea = false;
    /**
     * 选择时间最小值，单位秒
     */
    private long selectTimeMin = 1 * 60;
    /**
     * 选择时间最大值，单位秒
     */
    private long selectTimeMax = 10 * 60;
    /**
     * 当前日期的开始时间毫秒值
     */
    private long currentDateStartTimeMillis = DateUtils.getTodayStart(System.currentTimeMillis());
    /**
     * 视频时间段集合
     */
    private List<TimeSlot> vedioTimeSlot = new ArrayList<>();
    /**
     * 选择区域
     */
    private static float selectTimeAreaDistanceLeft = -1;//往左边选择的距离
    private static float selectTimeAreaDistanceRight = -1;//往右边选择的距离
    /**
     * 选择时间监听
     */
    private OnSelectedTimeListener onSelectedTimeListener;
    /**
     * 时间轴拖动
     */
    private OnBarMoveListener onBarMoveListener;

    public TimeRulerView(Context context) {
        this(context, null);
    }

    public TimeRulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeRulerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttr(attrs, defStyle);
        mScroller = new ScaleScroller(getContext(), this);
        setSurfaceTextureListener(this);
        initPaint();
        moveTimer();//开启移动定时器
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        smallRulerPaint.setAntiAlias(true);
        smallRulerPaint.setColor(rulerColor);
        smallRulerPaint.setStrokeWidth(rulerWidthSamll);

        largeRulerPaint.setAntiAlias(true);
        largeRulerPaint.setColor(rulerColor);
        largeRulerPaint.setStrokeWidth(rulerWidthBig);

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

        selectAreaPaint.setColor(selectTimeBorderColor);
        selectAreaPaint.setAntiAlias(true);
        selectAreaPaint.setStrokeCap(Paint.Cap.ROUND);
        selectAreaPaint.setStyle(Paint.Style.STROKE);
        selectAreaPaint.setStrokeWidth(selectTimeStrokeWidth);

        vedioArea.setColor(selectTimeAreaColor);
        vedioArea.setAntiAlias(true);
    }

    /**
     * 初始化属性
     *
     * @param attrs
     * @param defStyleAttr
     */
    private void initAttr(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.TimeRulerView, defStyleAttr, 0);
        int attrCount = a.getIndexCount();
        for (int index = 0; index < attrCount; index++) {
            int attr = a.getIndex(index);
            if (attr == R.styleable.TimeRulerView_centerLineColor) {//中轴线颜色
                centerLineColor = a.getColor(attr, centerLineColor);
            } else if (attr == R.styleable.TimeRulerView_centerLineSize) {//中轴线宽度
                centerLineWidth = (int) a.getDimension(attr, centerLineWidth);
            } else if (attr == R.styleable.TimeRulerView_vedioAreaColor) {// 视频区域颜色
                vedioBg = a.getColor(attr, vedioBg);
            } else if (attr == R.styleable.TimeRulerView_rulerTextColor) {// 刻度文本颜色
                textColor = a.getColor(attr, textColor);
            } else if (attr == R.styleable.TimeRulerView_rulerTextSize) {// 刻度文本大小
                textSize = (int) a.getDimension(attr, textSize);
            } else if (attr == R.styleable.TimeRulerView_rulerLineColor) {// 刻度线颜色
                rulerColor = a.getColor(attr, rulerColor);
                upAndDownLineColor = rulerColor;
            } else if (attr == R.styleable.TimeRulerView_selectTimeBorderColor) {// 选择时间的边框颜色
                selectTimeBorderColor = a.getColor(attr, selectTimeBorderColor);
            } else if (attr == R.styleable.TimeRulerView_selectTimeAreaColor) {// 已选时间区域颜色
                selectTimeAreaColor = a.getColor(attr, selectTimeAreaColor);
            } else if (attr == R.styleable.TimeRulerView_samllRulerLineWidth) {// 小刻度宽度
                rulerWidthSamll = (int) a.getDimension(attr, rulerWidthSamll);
            } else if (attr == R.styleable.TimeRulerView_samllRulerLineHeight) {// 小刻度高度
                rulerHeightSamll = (int) a.getDimension(attr, rulerHeightSamll);
            } else if (attr == R.styleable.TimeRulerView_largeRulerLineWidth) {// 大刻度宽度
                rulerWidthBig = (int) a.getDimension(attr, rulerWidthBig);
            } else if (attr == R.styleable.TimeRulerView_largeRulerLineHeight) {// 大刻度高度
                rulerHeightBig = (int) a.getDimension(attr, rulerHeightBig);
            } else if (attr == R.styleable.TimeRulerView_selectTimeBorderSize) {// 大刻度高度
                selectTimeStrokeWidth = a.getDimension(attr, selectTimeStrokeWidth);
            }
        }
        a.recycle();
    }

    private int scaleMode = ScaleMode.KEY_MINUTE;
    /**
     * 切换主线程
     */
    private static final int WHAT_MOVING = 447;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_MOVING:
                    onBarMoveListener.onBarMoving(getCurrentTimeMillis());
                    break;
            }
        }
    };

    public boolean isSelectTimeArea() {
        return isSelectTimeArea;
    }

    /**
     * 设置是否选择时间区域
     *
     * @param selectTimeArea
     */
    public void setSelectTimeArea(boolean selectTimeArea) {
        if (selectTimeArea) {//选择的时候需要停止选择
            if (scaleMode == ScaleMode.KEY_HOUSE) {
                scaleMode = ScaleMode.KEY_MINUTE;//要恢复到分钟模式，否则刻度精度太高无法选择
                rulerSpace = DEFAULT_RULER_SPACE;
            }
            isMoving = false;
        }
        selectTimeAreaDistanceLeft = -1;//需要复位
        selectTimeAreaDistanceRight = -1;//需要复位
        isSelectTimeArea = selectTimeArea;
//        refreshCanvas();
        setCurrentTimeMillisNoDelayed(getCurrentTimeMillis());
    }

    public void setOnBarMoveListener(OnBarMoveListener onBarMoveListener) {
        this.onBarMoveListener = onBarMoveListener;
    }

    public void setOnSelectedTimeListener(OnSelectedTimeListener onSelectedTimeListener) {
        this.onSelectedTimeListener = onSelectedTimeListener;
    }

    /**
     * 设置view高度
     */
    public void setViewHeightForDp(int view_height) {
        this.view_height = CUtils.dip2px(view_height);
        refreshCanvas();
    }

    /**
     * 设置当前时间的毫秒值.
     * 设置一个100ms的延迟，避免过快设置导致短时间内重复绘制
     *
     * @param currentTimeMillis
     */
    public void setCurrentTimeMillis(final long currentTimeMillis) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                currentDateStartTimeMillis = DateUtils.getTodayStart(currentTimeMillis);
                //设置最左边的时间为当前时间减view宽度一半所占的时间--->这个时间所在的像素值就是
                lastPix = -((currentTimeMillis - currentDateStartTimeMillis) / 1000f - getWidth() / 2f * pixSecond) / pixSecond;
                refreshCanvas();
            }
        }, 100);
    }

    /**
     * 设置当前时间的毫秒值.(没有延迟)
     * 设置一个100ms的延迟，避免过快设置导致短时间内重复绘制
     *
     * @param currentTimeMillis
     */
    private void setCurrentTimeMillisNoDelayed(long currentTimeMillis) {
        int itemWidth = rulerWidthSamll + rulerSpace;//单个view的宽度
        getPixSecond(itemWidth);
//        pixSecond = 60f / itemWidth;//itemWidth表示一分钟，
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

    private long currentTimeMillis;

    /**
     * 移动定时器
     */
    private void moveTimer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (isMoving) {
                    //(rulerWidthSamll + rulerSpace) / 60   表示1秒钟移动多少像素
                    if (scaleMode == ScaleMode.KEY_MINUTE) {
                        lastPix -= (rulerWidthSamll + rulerSpace) / 60.0;
                    } else {
                        lastPix -= (rulerWidthSamll + rulerSpace) / (10 * 60.0);
                    }
                    refreshCanvas();
                    if (onBarMoveListener != null) {
                        if (getCurrentTimeMillis() >= currentDateStartTimeMillis + 24 * 60 * 60 * 1000) {
                            onBarMoveListener.onBarMoveFinish(getCurrentTimeMillis());
                            setMoving(false);
                        } else {
                            mHandler.sendEmptyMessage(WHAT_MOVING);
                        }
                    }
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
    private void setMoving(boolean moving) {
        isMoving = moving;
    }

    /**
     * 打开移动
     */
    public void openMove() {
        setMoving(true);
    }

    /**
     * 关闭移动
     */
    public void closeMove() {
        setMoving(false);
    }

    //刷新视图
    private void refreshCanvas() {
        Canvas canvas = lockCanvas();
        if (canvas != null) {
            canvas.drawColor(bgColor);
            drawUpAndDownLine(canvas);
            drawTextAndRuler(canvas);//画文本和刻度
            drawRecodeArea(canvas);//画有效视频区域
            drawCenterLine(canvas);//画中间标线
            drawSelectTimeArea(canvas);//画视频选择区域
        }
        unlockCanvasAndPost(canvas);
    }

    /**
     * 画选择时间的区域
     *
     * @param canvas
     */
    private void drawSelectTimeArea(Canvas canvas) {
        if (isSelectTimeArea) {
            if (selectTimeAreaDistanceLeft == -1) {
                selectTimeAreaDistanceLeft = (getCurrentTimeMillis() - currentDateStartTimeMillis) / pixSecond / 1000f - 2.5f * 60 / pixSecond + lastPix;
            }
            if (selectTimeAreaDistanceRight == -1) {
                selectTimeAreaDistanceRight = (getCurrentTimeMillis() - currentDateStartTimeMillis) / pixSecond / 1000f + 2.5f * 60 / pixSecond + lastPix;
            }
            selectAreaPaint.setStrokeWidth(selectTimeStrokeWidth);
            canvas.drawLine(selectTimeAreaDistanceLeft, selectTimeStrokeWidth / 2, selectTimeAreaDistanceLeft, view_height - textSize - selectTimeStrokeWidth / 2, selectAreaPaint);
            canvas.drawLine(selectTimeAreaDistanceRight, selectTimeStrokeWidth / 2, selectTimeAreaDistanceRight, view_height - textSize - selectTimeStrokeWidth / 2, selectAreaPaint);
            selectAreaPaint.setStrokeWidth(selectTimeStrokeWidth / 3);
            canvas.drawLine(selectTimeAreaDistanceRight, 0, selectTimeAreaDistanceLeft, 0, selectAreaPaint);
            selectAreaPaint.setStrokeWidth(selectTimeStrokeWidth / 4);
            canvas.drawLine(selectTimeAreaDistanceRight, view_height - textSize - selectTimeStrokeWidth / 6, selectTimeAreaDistanceLeft, view_height - textSize - selectTimeStrokeWidth / 6, selectAreaPaint);
            //画带透明色的选择区域
            canvas.drawRect(selectTimeAreaDistanceLeft, 0, selectTimeAreaDistanceRight, view_height - textSize, vedioArea);
            //回调结果出去
            onSelectedTimeListener.onDragging(getSelectStartTime(), getSelectEndTime());
        }
    }

    /**
     * 获取选择的结束时间
     * 由于lastPix是负数，所以是-lastPix
     *
     * @return
     */
    public long getSelectEndTime() {
        if (selectTimeAreaDistanceRight == -1) {
            return currentDateStartTimeMillis + (long) (currentSecond * 1000) + (long) (2.5 * 60 * 1000);
        } else {
            return currentDateStartTimeMillis + (long) ((selectTimeAreaDistanceRight - lastPix) * pixSecond * 1000);
        }
    }

    /**
     * 获取选择的开始时间.
     * 由于lastPix是负数，所以是-lastPix
     *
     * @return
     */
    public long getSelectStartTime() {
        if (selectTimeAreaDistanceLeft == -1) {
            return currentDateStartTimeMillis + (long) (currentSecond * 1000) - (long) (2.5 * 60 * 1000);
        } else {
            return currentDateStartTimeMillis + (long) ((selectTimeAreaDistanceLeft + selectTimeStrokeWidth / 2 - lastPix) * pixSecond * 1000);
        }
    }

    /**
     * 画视频区域
     *
     * @param canvas
     */
    private void drawRecodeArea(Canvas canvas) {
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
        int viewWidth = getWidth();
        int itemWidth = rulerWidthSamll + rulerSpace;//单个view的宽度
        getPixSecond(itemWidth);
        int count = viewWidth / itemWidth;
        if (lastPix < 0) {//<0表示往左边移动-->右滑
            count += -lastPix / 10;//需要加上移动的距离
        }
        int leftCound = 0;
        if (scaleMode == ScaleMode.KEY_MINUTE) {
            if (getCurrentTimeMillis() < currentDateStartTimeMillis + 15 * 60 * 1000) {//15分钟内的多画上一天的60
                leftCound = -60;//多花左边的60分钟
            }
        } else {
            if (getCurrentTimeMillis() < currentDateStartTimeMillis + 6 * 60 * 60 * 1000) {//15分钟内的多画上一天的60
                leftCound = -60;//多花左边的60分钟
            }
        }
        //从屏幕左边开始画刻度和文本，从上一天的23:30开始
        for (int index = leftCound; index < count; index++) {
            float rightX = index * itemWidth + lastPix;//右边方向x坐标
            if (index == 0) {//根据最左边的时刻算最中间的时刻，左边时刻(rightX*每秒多少像素)+中间时刻（view的宽度/2*每秒多少像素）
                if (rightX < 0) {//15分钟之后的移动
                    currentSecond = viewWidth * pixSecond / 2f + Math.abs(rightX * pixSecond);
                } else {//15分钟之前的移动
                    currentSecond = viewWidth * pixSecond / 2f - rightX * pixSecond;
                }
                lastConfigChangedTime = getCurrentTimeMillis();//记录切换前的时间
            }
            int divisor;//除数、刻度精度
            switch (scaleMode) {
                case ScaleMode.KEY_HOUSE:
                    divisor = 6;
                    break;
                case ScaleMode.KEY_MINUTE:
                    divisor = 10;
                    break;
                default:
                    divisor = 10;
            }
            if (index % divisor == 0) {//大刻度
                //画上边刻度
                canvas.drawLine(rightX, 0, rightX, rulerHeightBig, largeRulerPaint);
                //画下边刻度
                canvas.drawLine(rightX, view_height - textSize, rightX, view_height - rulerHeightBig - textSize, largeRulerPaint);
                //画文本
                draText(canvas, index * 60, rightX);
            } else {//小刻度
                //画上面小刻度
                canvas.drawLine(rightX, 0, rightX, rulerHeightSamll, smallRulerPaint);
                //画下面小刻度
                canvas.drawLine(rightX, view_height - textSize, rightX, view_height - rulerHeightSamll - textSize, smallRulerPaint);
            }
        }
    }

    /**
     * 根据缩放精度获取每格表示是多少分钟
     *
     * @param itemWidth
     */
    private void getPixSecond(int itemWidth) {
        if (scaleMode == ScaleMode.KEY_MINUTE) {
            pixSecond = 60f / itemWidth;//itemWidth表示一分钟，
        } else {
            pixSecond = 10 * 60f / itemWidth;//itemWidth表示一分钟，
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
        if (scaleMode == ScaleMode.KEY_MINUTE) {
            if (time < 0) {
                keyText = DateUtils.getTimeByCurrentSecond(24 * 60 * 60 + time);
            } else {
                keyText = DateUtils.getTimeByCurrentSecond(time);
            }
        } else {
            if (time < 0) {
                keyText = DateUtils.getTimeByCurrentHours(24 * 60 * 60 + time);
            } else {
                keyText = DateUtils.getTimeByCurrentHours(time);
            }
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
        if (heightMode == MeasureSpec.EXACTLY) {//布局里面设置大小，其他都是默认值
            view_height = heightSize;
        }
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isSelectTimeArea) {//选择视频的时候 下面的时间刻度是禁止滑动的
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    float curX = event.getX();//拿到当前的x轴
                    if (Math.abs(curX - selectTimeAreaDistanceLeft) < Math.abs(curX - selectTimeAreaDistanceRight)) {//左边
                        //1-10分钟
                        float currentInterval = (selectTimeAreaDistanceRight - selectTimeStrokeWidth - curX) * pixSecond;//当前时间间隔
                        if (selectTimeMin < currentInterval && currentInterval < selectTimeMax) {
                            selectTimeAreaDistanceLeft = curX;
                            //实时地将结果回调出去
                            if (onSelectedTimeListener != null) {
                                onSelectedTimeListener.onDragging(getSelectStartTime(), getSelectEndTime());
                            }
                        } else {
                            //实时地将结果回调出去
                            if (currentInterval >= selectTimeMax) {
                                onSelectedTimeListener.onMaxTime();
                            } else if (currentInterval <= selectTimeMin) {
                                onSelectedTimeListener.onMinTime();
                            }
                        }
                    } else {//右边
                        //1-10分钟
                        float currentInterval = (curX - (selectTimeAreaDistanceLeft + selectTimeStrokeWidth)) * pixSecond;//当前时间间隔
                        if (selectTimeMin < currentInterval && currentInterval < selectTimeMax) {
                            selectTimeAreaDistanceRight = curX;
                            //实时地将结果回调出去
                            if (onSelectedTimeListener != null) {
                                onSelectedTimeListener.onDragging(getSelectStartTime(), getSelectEndTime());
                            }
                        } else {
                            //实时地将结果回调出去
                            if (onSelectedTimeListener != null) {
                                if (currentInterval >= selectTimeMax) {
                                    onSelectedTimeListener.onMaxTime();
                                } else if (currentInterval <= selectTimeMin) {
                                    onSelectedTimeListener.onMinTime();
                                }
                            }
                        }
                    }
                    refreshCanvas();//重绘
                    break;
            }
        } else {
            mScroller.onTouchEvent(event);
        }
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
        Log.e("hdltag", "onScroll(TimeRulerView.java:671):开始滑动了");
        onBarMoveListener.onDragBar(distance > 0, getCurrentTimeMillis());
        lastPix += distance;
        refreshCanvas();
    }

    @Override
    public void onZoomFinished() {

    }


    /**
     * 滑动结束
     */
    @Override
    public void onScrollFinished() {
        Log.e("hdltag", "onScrollFinished(TimeRulerView.java:685):滑动结束了");
        if (currentDateStartTimeMillis <= getCurrentTimeMillis() && getCurrentTimeMillis() <= (currentDateStartTimeMillis + 24 * 60 * 60 * 1000 - 2000)) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (onBarMoveListener != null) {
                        onBarMoveListener.onBarMoveFinish(getCurrentTimeMillis());
                    }
                }
            }, 1000);
        } else if (currentDateStartTimeMillis >= getCurrentTimeMillis()) {
            setCurrentTimeMillis(currentDateStartTimeMillis);
            if (onBarMoveListener != null) {
                onBarMoveListener.onMoveExceedStartTime();
            }
        } else if (getCurrentTimeMillis() >= (currentDateStartTimeMillis + 24 * 60 * 60 * 1000 - 1000)) {
            setCurrentTimeMillis(currentDateStartTimeMillis + 24 * 60 * 60 * 1000 - 1000);
            if (onBarMoveListener != null) {
                onBarMoveListener.onMoveExceedEndTime();
            }
        }
    }

    /**
     * 缩放时
     *
     * @param mScale
     * @param time
     */
    @Override
    public void onZoom(float mScale, double time) {
        //控制缩放比例
        if (mScale > 1) {
            if (rulerSpace < MAX_SCALE) {
                rulerSpace++;
            }
        } else {
            if (rulerSpace > MIN_SCALE) {
                rulerSpace--;
            }
        }
        //改变刻度文字精度
        if (rulerSpace <= CUtils.dip2px(10)) {
            scaleMode = ScaleMode.KEY_HOUSE;//小时
        } else {
            scaleMode = ScaleMode.KEY_MINUTE;//分钟
        }
        setCurrentTimeMillisNoDelayed(getCurrentTimeMillis());//实时设置到当前时间
    }

    private long lastConfigChangedTime;//横竖屏切换时的时间--->让横竖屏时间一致

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

    /**
     * ---------------------------------------------
     * 从这里开始以下都是属性的设置
     * ---------------------------------------------
     *
     * @param rulerColor
     */
    public void setRulerColor(int rulerColor) {
        this.rulerColor = rulerColor;
    }

    public void setRulerWidthSamll(int rulerWidthSamll) {
        this.rulerWidthSamll = rulerWidthSamll;
    }

    public void setRulerHeightSamll(int rulerHeightSamll) {
        this.rulerHeightSamll = rulerHeightSamll;
    }

    public void setRulerWidthBig(int rulerWidthBig) {
        this.rulerWidthBig = rulerWidthBig;
    }

    public void setRulerHeightBig(int rulerHeightBig) {
        this.rulerHeightBig = rulerHeightBig;
    }

    public void setUpAndDownLineWidth(int upAndDownLineWidth) {
        this.upAndDownLineWidth = upAndDownLineWidth;
    }

    public void setUpAndDownLineColor(int upAndDownLineColor) {
        this.upAndDownLineColor = upAndDownLineColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setCenterLineColor(int centerLineColor) {
        this.centerLineColor = centerLineColor;
    }

    public void setCenterLineWidth(int centerLineWidth) {
        this.centerLineWidth = centerLineWidth;
    }

    public void setVedioBg(int vedioBg) {
        this.vedioBg = vedioBg;
    }

    public void setSelectTimeBorderColor(int selectTimeBorderColor) {
        this.selectTimeBorderColor = selectTimeBorderColor;
    }

    public void setSelectTimeAreaColor(int selectTimeAreaColor) {
        this.selectTimeAreaColor = selectTimeAreaColor;
    }

    public void setSelectTimeStrokeWidth(float selectTimeStrokeWidth) {
        this.selectTimeStrokeWidth = selectTimeStrokeWidth;
    }

}
