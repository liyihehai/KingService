package com.nnte.AutoTask;

/**
 * 任务运行时间控制
 * */
public class TaskRunTime {
    /**
     * 时间间隔类型:Month  -- 月  ：表示intervalCount月后运行一次
     *             Week   -- 周  ：表示intervalCount周后运行一次
     *             Day    -- 天  ：表示intervalCount天后运行一次
     *             Hour   -- 小时：表示intervalCount小时后运行一次
     *             Minute -- 分钟：表示intervalCount分钟后运行一次
     *             Second -- 秒钟：表示intervalCount秒钟后运行一次
     */
    private String intervalType;
    private int intervalCount;   //间隔数量 >=1
    /**
     * 如果时间间隔类型为Month,monthStartDay必须设置
     * */
    private int monthStartDay;  //月内开始日期 >=1 && <=31
    /**
     * 如果时间间隔类型为Week,weekStartDay必须设置
     * */
    private int weekStartDay;   //周内开始日期 >=0 && <=6
    /**
     * 如果时间间隔类型为Month、Week、Day，以下三个参数有效
     * 程序应先计算上次执行时间加间隔数量的月、周、天的日期，然后加上指定的时、分、秒表示
     * 下次执行的时间
     * */
    private int dayStartHour;   // 日内开始小时数 >=0 && <=23
    private int dayStartMinute; // 日内开始分钟数 >=0 && <=59
    private int dayStartSecond; // 日内开始秒钟数 >=0 && <=59

    public String getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }

    public int getIntervalCount() {
        return intervalCount;
    }

    public void setIntervalCount(int intervalCount) {
        this.intervalCount = intervalCount;
    }

    public void setDayStartHour(int dayStartHour) {
        this.dayStartHour = dayStartHour;
    }

    public int getDayStartMinute() {
        return dayStartMinute;
    }

    public void setDayStartMinute(int dayStartMinute) {
        this.dayStartMinute = dayStartMinute;
    }

    public int getDayStartSecond() {
        return dayStartSecond;
    }

    public void setDayStartSecond(int dayStartSecond) {
        this.dayStartSecond = dayStartSecond;
    }

    public int getDayStartHour() {
        return dayStartHour;
    }

    public int getMonthStartDay() {
        return monthStartDay;
    }

    public void setMonthStartDay(int monthStartDay) {
        this.monthStartDay = monthStartDay;
    }

    public int getWeekStartDay() {
        return weekStartDay;
    }

    public void setWeekStartDay(int weekStartDay) {
        this.weekStartDay = weekStartDay;
    }
}
