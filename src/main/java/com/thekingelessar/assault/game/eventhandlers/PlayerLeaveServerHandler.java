package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveServerHandler implements Listener
{
    @EventHandler
    public void onPlayerLeaveServer(PlayerQuitEvent playerQuitEvent)
    {
        Player player = playerQuitEvent.getPlayer();
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        
        if (gameInstance != null)
        {
            gameInstance.removePlayer(player);
        }
    }
}
