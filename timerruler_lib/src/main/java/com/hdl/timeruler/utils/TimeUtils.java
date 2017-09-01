package com.hdl.timeruler.utils;

/**
 * Created by HDL on 2017/8/21.
 */

public class TimeUtils {
    /**
     * 根据当前时间值获取时间.(eg:index=10--->00:10)
     *
     * @param index
     * @return
     */
    public static String getTime(int index) {
        int minute = index % 60;
        int hour = index / 60;
        if (hour >= 24) {
            hour = hour % 24;
        }
        return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
    }

    /**
     * 根据当前的秒数计算时间
     *
     * @param currentSecond
     * @return
     */
    public static String getTimeByCurrentSecond(int currentSecond) {
        currentSecond = currentSecond / 60;
        int minute = currentSecond % 60;
        int hour = currentSecond / 60;
        if (hour >= 24) {
            hour = hour % 24;
        }
        return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
    }

    public static void main(String[] args) {
        System.out.println(getTimeByCurrentSecond(120));
    }
}
