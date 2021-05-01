package com.thekingelessar.assault.game.eventhandlers;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.map.MapBase;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.util.Coordinate;
import com.thekingelessar.assault.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PlayerBlockPlaceHandler implements Listener
{
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent)
    {
        if (blockPlaceEvent.getBlockPlaced().getLocation().getZ() > 88 || blockPlaceEvent.getBlockPlaced().getLocation().getZ() < 2)
        {
            blockPlaceEvent.setCancelled(true);
            return;
        }
        
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
            
            for (GameTeam gameTeam : gameInstance.teams.values())
            {
                MapBase mapBase = gameTeam.mapBase;
                if (Util.isInside(placedLocation, mapBase.defenderBoundingBox.get(0).toLocation(gameInstance.gameWorld), mapBase.defenderBoundingBox.get(1).toLocation(gameInstance.gameWorld)))
                {
                    blockPlaceEvent.setCancelled(true);
                    return;
                }
            }
            
            Coordinate placedCoordinate = new Coordinate(placedLocation.getBlockX(), placedLocation.getBlockY(), placedLocation.getBlockZ());
            
            gameInstance.placedBlocks.add(placedCoordinate);
            
            if (blockPlaceEvent.getBlock().getType().equals(Material.TNT))
            {
                Block placedBlock = blockPlaceEvent.getBlockPlaced();
                Location location = new Location(placedBlock.getWorld(), placedBlock.getX() + .5, placedBlock.getY() + .1, placedBlock.getZ() + .5);
                
                TNTPrimed tntPrimed = (TNTPrimed) player.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
                Vector vector = tntPrimed.getVelocity();
                vector.setX(0);
                vector.setY(.25);
                vector.setZ(0);
                tntPrimed.setVelocity(vector);
                
                blockPlaceEvent.setCancelled(true);
                
                ItemStack tntItem = blockPlaceEvent.getItemInHand();
                tntItem.setAmount(tntItem.getAmount() - 1);
                player.getInventory().setItemInHand(tntItem);
            }
        }
    }
}