package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.WorldManager;
import com.thekingelessar.assault.config.Map;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerJoinWorldHandler implements Listener
{
    @EventHandler
    public void onPlayerJoinWorld(PlayerChangedWorldEvent playerChangedWorldEvent) {
        Player player = playerChangedWorldEvent.getPlayer();
        World newWorld = player.getWorld();
        
        if(WorldManager.worldGames.containsKey(newWorld.getName())) {
            Map worldMap = Assault.maps.get(newWorld.getName());
            player.teleport(new Location(newWorld, worldMap.waitingPlatform.x, worldMap.waitingPlatform.y, worldMap.waitingPlatform.z));
        }
    }
}
