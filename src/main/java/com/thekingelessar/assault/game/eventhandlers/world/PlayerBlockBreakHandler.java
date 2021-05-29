package com.thekingelessar.assault.game.eventhandlers.world;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.util.Coordinate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerBlockBreakHandler implements Listener
{
    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent)
    {
        Player player = blockBreakEvent.getPlayer();
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        
        if (gameInstance != null)
        {
            PlayerMode playerMode = PlayerMode.getPlayerMode(player);
            
            if (playerMode != null)
            {
                if (!playerMode.canBreakBlocks)
                {
                    blockBreakEvent.setCancelled(true);
                    return;
                }
            }
            
            blockBreakEvent.setCancelled(true);
            
            Location brokenLocation = blockBreakEvent.getBlock().getLocation();
            Coordinate brokenCoordinate = new Coordinate(brokenLocation.getBlockX(), brokenLocation.getBlockY(), brokenLocation.getBlockZ());
            
            for (Coordinate placedCoord : gameInstance.placedBlocks)
            {
                if (placedCoord.x == brokenCoordinate.x && placedCoord.y == brokenCoordinate.y && placedCoord.z == brokenCoordinate.z)
                {
                    gameInstance.placedBlocks.remove(brokenCoordinate);
                    blockBreakEvent.setCancelled(false);
                }
            }
            
            if (gameInstance.gameMap.breakableBlocks.contains(blockBreakEvent.getBlock().getType()))
            {
                blockBreakEvent.setCancelled(false);
            }
            
            player.getItemInHand().setDurability(player.getItemInHand().getType().getMaxDurability());
        }
    }
}