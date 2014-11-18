package com.kaltiz.ItsAboutTime;

public class Time
{
    private int year;
    private int month;
    private int dayOfMonth;
    private int dayOfYear;
    private int weekDay;
    private int hour;
    private int minute;
    private int second;

    public Time(int year, int month, int dayOfMonth, int dayOfYear, int weekDay, int hour, int minute, int second)
    {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.dayOfYear = dayOfYear;
        this.weekDay = weekDay;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getDayOfYear() {
        return dayOfYear;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }
}
