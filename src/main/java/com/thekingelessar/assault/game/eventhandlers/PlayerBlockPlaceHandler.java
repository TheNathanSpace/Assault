package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.PlayerMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerBlockPlaceHandler implements Listener
{
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent)
    {
        Player player = blockPlaceEvent.getPlayer();
        
        PlayerMode playerMode = PlayerMode.getPlayerMode(player);
        
        if (playerMode != null)
        {
            
            if (!playerMode.canPlaceBlocks)
            {
                blockPlaceEvent.setCancelled(true);
            }
        }
        
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        if (gameInstance != null)
        {
            if (!gameInstance.gameMap.placeableBlocks.contains(blockPlaceEvent.getBlock().getType()))
            {
                blockPlaceEvent.setCancelled(true);
                
                // Todo: error messages for player (this and block break)
            }
        }
        
    }
}