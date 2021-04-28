package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.util.Coordinate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
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
                return;
            }
        }
        
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        if (gameInstance != null)
        {
            Location placedLocation = blockPlaceEvent.getBlock().getLocation();
            Coordinate placedCoordinate = new Coordinate(placedLocation.getBlockX(), placedLocation.getBlockY(), placedLocation.getBlockZ());
            
            gameInstance.placedBlocks.add(placedCoordinate);
            
//            blockPlaceEvent.setCancelled(true);
//
//            for (Material block : gameInstance.gameMap.placeableBlocks)
//            {
//                if (block.equals(blockPlaceEvent.getBlock().getType()))
//                {
//                    blockPlaceEvent.setCancelled(false);
//                }
//            }
            
            if (blockPlaceEvent.getBlock().getType().equals(Material.TNT))
            {
                Block placedBlock = blockPlaceEvent.getBlock();
                Location location = new Location(placedBlock.getWorld(), placedBlock.getX() - .5, placedBlock.getY() - .5, placedBlock.getZ() - .5);
                player.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
            }
        }
    }
}