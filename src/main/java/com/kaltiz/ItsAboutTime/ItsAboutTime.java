package com.kaltiz.ItsAboutTime;

import com.kaltiz.ItsAboutTime.listeners.PlayerWatchListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import javax.management.remote.rmi._RMIConnection_Stub;
import java.util.logging.Logger;

public class ItsAboutTime extends JavaPlugin
{
    private World defaultWorld;
    private String doDaylightCycleDefault;
    private Logger log;
    private TimeCalculator timecalc;

    @Override
    public void onEnable()
    {
        log = this.getLogger();

        defaultWorld = Bukkit.getWorld("World");
        doDaylightCycleDefault = defaultWorld.getGameRuleValue("doDaylightCycle");

        this.timecalc = new TimeCalculator(this);

        // Save the default config
        saveDefaultConfig();

        if (!this.timecalc.readConfig())
        {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Setup the player watch listener if enabled
        if(getConfig().getBoolean("use-watch"))
            getServer().getPluginManager().registerEvents(new PlayerWatchListener(this), this);

        // Ensure that the time is set to not move
       defaultWorld.setGameRuleValue("doDaylightCycle", "false");

        // Setup the timesettask
    }

    @Override
    public void onDisable()
    {
        // Stop the timesettask

        // Set the doDayLightCycle back to normal
        defaultWorld.setGameRuleValue("doDaylightCycle", doDaylightCycleDefault);
    }

    public TimeCalculator getTimeCalculator()
    {
        return this.timecalc;
    }

    public World getDefaultWorld()
    {
        return defaultWorld;
    }
}
