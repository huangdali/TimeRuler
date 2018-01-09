package com.hdl.timeruler;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 上一天，下一天切换提示
 * Created by HDL on 2018/1/9.
 *
 * @author HDL
 */

public class TipView extends RelativeLayout {
    private ImageView ivLeft, ivRight;
    private ImageView ivTipLeft, ivTipRight;
    /**
     * 是否显示左边（上一天）提示
     */
    private boolean isShowLeftTip = false;
    /**
     * 是否显示右边（下一天）提示
     */
    private boolean isShowRightTip = false;

    private ObjectAnimator leftAnimation;
    private ObjectAnimator rightAnimation;

    public TipView(Context context) {
        this(context, null);
    }

    public TipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View inflate = View.inflate(context, R.layout.tip_layout, null);
        addView(inflate);
        ivLeft = (ImageView) inflate.findViewById(R.id.iv_left);
        ivRight = (ImageView) inflate.findViewById(R.id.iv_right);
        ivTipLeft = (ImageView) inflate.findViewById(R.id.iv_tip_left);
        ivTipRight = (ImageView) inflate.findViewById(R.id.iv_tip_right);
        leftAnimation = ObjectAnimator.ofFloat(ivLeft, "Alpha", 0, 1, 0, 1);
        rightAnimation = ObjectAnimator.ofFloat(ivRight, "Alpha", 0, 1, 0, 1);
        leftAnimation.setDuration(3000);
        rightAnimation.setDuration(3000);
        leftAnimation.setRepeatMode(ValueAnimator.RESTART);
        rightAnimation.setRepeatMode(ValueAnimator.RESTART);
        setShowLeftTip(false);
        setShowRightTip(false);

    }

    public boolean isShowLeftTip() {
        return isShowLeftTip;
    }

    public void setShowLeftTip(boolean showLeftTip) {
        isShowLeftTip = showLeftTip;
        if (isShowLeftTip) {
            ivTipLeft.setVisibility(VISIBLE);
            ivLeft.setVisibility(VISIBLE);
            leftAnimation.start();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setShowLeftTip(false);
                }
            }, 3000);
        } else {
            ivTipLeft.setVisibility(GONE);
            ivLeft.setVisibility(GONE);
            leftAnimation.cancel();
        }
    }

    public boolean isShowRightTip() {
        return isShowRightTip;
    }

    public void setShowRightTip(boolean showRightTip) {
        isShowRightTip = showRightTip;
        if (isShowRightTip) {
            ivRight.setVisibility(VISIBLE);
            ivTipRight.setVisibility(VISIBLE);
            rightAnimation.start();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setShowRightTip(false);
                }
            }, 3000);
        } else {
            ivRight.setVisibility(GONE);
            ivTipRight.setVisibility(GONE);
            rightAnimation.cancel();
        }
    }

}
