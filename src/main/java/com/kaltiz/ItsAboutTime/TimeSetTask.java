package com.kaltiz.ItsAboutTime;

public class TimeSetTask implements Runnable
{
    private ItsAboutTime plugin;

    public TimeSetTask(ItsAboutTime plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        Time time = plugin.getTimeCalculator().calculateTime();
        int hourInDay = plugin.getConfig().getInt("hours-in-day");
        int minutesInHours = plugin.getConfig().getInt("minutes-in-hour");

        long ticksPerHour = 24000 / hourInDay;
        long currentTime = (((time.getHour() / (hourInDay/24)) * 1000) - 6000) + ((ticksPerHour / minutesInHours) * time.getMinute());

        plugin.getLogger().info("currentTime " + currentTime);

        plugin.getDefaultWorld().setTime(currentTime);
    }
}
