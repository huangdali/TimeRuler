package com.hdl.timeruler;

import com.hdl.timeruler.utils.DateUtils;

/**
 * 时间段
 * Created by HDL on 2017/9/4.
 */

public class TimeSlot {
    /**
     * 开始时间
     */
    private long startTime;
    /**
     * 结束时间
     */
    private long endTime;

    public TimeSlot(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * 获取开始时间.
     * 当天持续秒数---->减去了当前开始时间的毫秒值（eg  00:01:00---->60）
     *
     * @return
     */
    public float getStartTime() {
        return (startTime - DateUtils.getTodayStart(startTime)) / 1000f;
    }

    /**
     * 获取结束时间
     * 当天持续秒数---->减去了当前开始时间的毫秒值（eg  00:01:00---->60）
     *
     * @return
     */
    public float getEndTime() {
        return (endTime - DateUtils.getTodayStart(endTime)) / 1000f;
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }
}
