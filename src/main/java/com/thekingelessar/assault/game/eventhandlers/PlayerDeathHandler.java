package com.thekingelessar.assault.game.eventhandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathHandler implements Listener
{
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent playerDeathEvent)
    {
        Player playerKilled = playerDeathEvent.getEntity();
    
    }
}
