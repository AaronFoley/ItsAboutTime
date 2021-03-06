package com.kaltiz.ItsAboutTime;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimeCalculator
{
    private ItsAboutTime plugin;

    private int daysInWeek;
    private int monthInYear;
    private List<Integer> daysInMonth;
    private int secondsInMinute;
    private int minutesInHours;
    private int hourInDay;

    private double realYearsInYear;
    private int yearModifier;

    private List<String> monthNames;
    private List<String> weekdayNames;

    private String timezone;

    private String formatStr;

    private int updateTime;

    private Time cached = null;
    private Long cachedTime = 0L;
    private long cacheHoldTime;

    public TimeCalculator(ItsAboutTime plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Checks and reads the config, returns false on error
     * @return
     */
    public boolean readConfig()
    {
        daysInWeek = plugin.getConfig().getInt("days-in-week");
        monthInYear = plugin.getConfig().getInt("months-in-year");
        daysInMonth = plugin.getConfig().getIntegerList("days-in-month");
        secondsInMinute = plugin.getConfig().getInt("seconds-in-minute");
        minutesInHours = plugin.getConfig().getInt("minutes-in-hour");
        hourInDay = plugin.getConfig().getInt("hours-in-day");

        realYearsInYear = plugin.getConfig().getDouble("year-length");
        yearModifier = plugin.getConfig().getInt("year-modifier");

        monthNames = plugin.getConfig().getStringList("month-names");
        weekdayNames = plugin.getConfig().getStringList("weekday-names");

        timezone = plugin.getConfig().getString("timezone");
        formatStr = plugin.getConfig().getString("time-format");

        cacheHoldTime = plugin.getConfig().getLong("clock-cache");

        updateTime = plugin.getConfig().getInt("update-interval");

        // Check days in month length, should be equal to months in year
        if (daysInMonth.size() != monthInYear)
        {
            plugin.getLogger().severe("There should be an entry for each month in days-in-month");
            return false;
        }

        // Check the month names, should be equal to months in year
        if (monthNames.size() != daysInWeek)
        {
            plugin.getLogger().severe("There should be an entry for each month in month-names");
            return false;
        }

        // Check the weekday names, should be equal to days in week
        if (weekdayNames.size() != daysInWeek)
        {
            plugin.getLogger().severe("There should be an entry for each day in weekday-names");
            return false;
        }

        return true;
    }

    public Time calculateTime()
    {
        if ((this.cachedTime + this.cacheHoldTime) > System.currentTimeMillis()/1000 && this.cached != null)
            return this.cached;

        // Get the current time
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setTimeZone(TimeZone.getTimeZone(timezone));

        // Calculate the year
        int year = cal.get(Calendar.YEAR);
        year = (int) (year / realYearsInYear);
        year = year + yearModifier;

        // Turn Real days into hours
        double realDaysInHours = (cal.get(Calendar.DAY_OF_YEAR) - 1) * 24;

        // Catch Spare Days through the real day
        double calhours = cal.get(Calendar.HOUR_OF_DAY);
        double calminutes = (double) cal.get(Calendar.MINUTE) / 60;
        double calseconds = (double) cal.get(Calendar.SECOND) / 3600;

        realDaysInHours += (double) (calhours + calminutes + calseconds);

        // Work out days in the year;
        int daysInYear = 0;
        for (int days : daysInMonth)
        {
            daysInYear += days;
        }

        // Work out the real hours in a fake day
        // Days become slightly longer in a leap year
        double realHoursInDay = (double) (cal.getActualMaximum(Calendar.DAY_OF_YEAR) * 24) / daysInYear;

        // Take out any leftover hours
        Double Ahour = (double) realDaysInHours / realHoursInDay;

        // Now convert the realDaysInHours to fake days
        // Note 0 indexed
        int dayInYear = (int) Math.floor(Ahour);

        // Now we figure out the month
        int totalDays = 0;
        int month = 0;
        for (int days : daysInMonth)
        {
            if (dayInYear >= totalDays && dayInYear < totalDays + days)
            {
                break;
            }
            month++;
            totalDays += days;
        }

        // Now lets workout the time
        // Extract fulldays
        double fragment = Double.parseDouble("." + String.valueOf(Ahour).split("\\.")[1]);

        // hourFrag is now in days, so times it by the number of hours in a day
        Double Amin = fragment * hourInDay;
        int hours = (int) Math.floor(Amin);

        // Amin is now in hours, get the number of minutes
        fragment = Double.parseDouble("." + String.valueOf(Amin).split("\\.")[1]);
        Double Asecs = fragment * minutesInHours;
        int minutes = (int) Math.floor(Asecs);

        // Asecs is now in minutes, get the number of minutes
        fragment = Double.parseDouble("." + String.valueOf(Asecs).split("\\.")[1]);
        int seconds = (int) Math.floor(fragment * secondsInMinute);

        // Now make a time object
        Time time = new Time(year, month + 1, dayInYear + 1 - totalDays, dayInYear + 1, (dayInYear + 1) % daysInWeek, hours, minutes, seconds);

        this.cached = time;
        this.cachedTime = System.currentTimeMillis()/1000;

        return time;
    }

    public String format(Time time)
    {
        String ret = this.formatStr;

        ret = ret.replaceAll("%d", leadingZeros(time.getDayOfMonth()));
        ret = ret.replaceAll("%D", String.valueOf(time.getDayOfMonth()));
        ret = ret.replaceAll("%l", weekdayNames.get(time.getWeekDay()));
        ret = ret.replaceAll("%o", this.getOrdinal(time.getDayOfMonth()));

        ret = ret.replaceAll("%F", monthNames.get(time.getMonth() -1));
        ret = ret.replaceAll("%m", leadingZeros(time.getMonth()));
        ret = ret.replaceAll("%M", String.valueOf(time.getMonth()));

        ret = ret.replaceAll("%y", String.valueOf(time.getYear()));
        ret = ret.replaceAll("%Y", String.valueOf(time.getYear()).substring(String.valueOf(time.getYear()).length() - 2));

        String ampm;
        if (time.getHour() > (hourInDay / 2))
            ampm = "pm";
        else
            ampm = "am";

        ret = ret.replaceAll("%a", ampm);
        ret = ret.replaceAll("%A", ampm.toUpperCase());
        ret = ret.replaceAll("%g", String.valueOf(time.getHour() - (hourInDay / 2)));
        ret = ret.replaceAll("%G", String.valueOf(time.getHour()));
        ret = ret.replaceAll("%h", leadingZeros(time.getHour() - (hourInDay / 2)));
        ret = ret.replaceAll("%H", leadingZeros(time.getHour()));
        ret = ret.replaceAll("%i", leadingZeros(time.getMinute()));
        ret = ret.replaceAll("%I", String.valueOf(time.getMinute()));
        ret = ret.replaceAll("%s", leadingZeros(time.getSecond()));
        ret = ret.replaceAll("%S", String.valueOf(time.getSecond()));

        ret = ret.replaceAll("%%", "%");

        return ret;
    }

    public String getOrdinal(int value)
    {
        int hundredRemainder = value % 100;
        if(hundredRemainder >= 10 && hundredRemainder <= 20) {
            return "th";
        }
        int tenRemainder = value % 10;
        switch (tenRemainder) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public String leadingZeros(int value)
    {
        if (value < 10)
        {
            return "0" + String.valueOf(value);
        }
        return String.valueOf(value);
    }
}
