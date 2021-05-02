package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.util.Coordinate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class GravelFallHandler implements Listener
{
    @EventHandler
    public void onGravelFall(EntityChangeBlockEvent entityChangeBlockEvent)
    {
        if (entityChangeBlockEvent.getEntity() instanceof FallingBlock)
        {
            FallingBlock fallingBlock = (FallingBlock) entityChangeBlockEvent.getEntity();
            Block block = entityChangeBlockEvent.getBlock();
            
            if (fallingBlock.getMaterial().equals(Material.GRAVEL) && block.getType().equals(Material.GRAVEL))
            {
                Location placedLocation = fallingBlock.getLocation();
                Coordinate placedCoordinate = new Coordinate(placedLocation.getBlockX(), placedLocation.getBlockY(), placedLocation.getBlockZ());
                
                GameInstance gameInstance = GameInstance.getWorldGameInstance(fallingBlock.getWorld());
                
                if (gameInstance != null)
                {
                    gameInstance.placedBlocks.remove(placedCoordinate);
                }
            }
            else if (fallingBlock.getMaterial().equals(Material.GRAVEL) && block.getType().equals(Material.AIR))
            {
                Location placedLocation = fallingBlock.getLocation();
                Coordinate placedCoordinate = new Coordinate(placedLocation.getBlockX(), placedLocation.getBlockY(), placedLocation.getBlockZ());
                
                GameInstance gameInstance = GameInstance.getWorldGameInstance(fallingBlock.getWorld());
                
                if (gameInstance != null)
                {
                    gameInstance.placedBlocks.add(placedCoordinate);
                }
            }
        }
    }
}