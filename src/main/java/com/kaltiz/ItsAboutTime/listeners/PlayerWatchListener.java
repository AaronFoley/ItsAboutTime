package com.kaltiz.ItsAboutTime.listeners;

import com.kaltiz.ItsAboutTime.ItsAboutTime;
import com.kaltiz.ItsAboutTime.Time;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerWatchListener implements Listener
{
    private ItsAboutTime plugin;

    public PlayerWatchListener(ItsAboutTime plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerUse(PlayerInteractEvent event) {
        Player p = event.getPlayer();

        if (p.getItemInHand().getType() == Material.WATCH)
        {
            Time time = plugin.getTimeCalculator().calculateTime();

            p.sendMessage(plugin.getTimeCalculator().format(time));
        }
    }
}