package com.thekingelessar.assault.game.eventhandlers.world;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.player.PlayerMode;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.game.team.TeamStage;
import com.thekingelessar.assault.game.world.map.MapBase;
import com.thekingelessar.assault.util.Coordinate;
import com.thekingelessar.assault.util.Util;
import com.thekingelessar.assault.util.xsupport.XMaterial;
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

import java.util.Arrays;
import java.util.List;

public class PlayerBlockPlaceHandler implements Listener
{
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent)
    {
        Player player = blockPlaceEvent.getPlayer();
        GameInstance gameInstance = GameInstance.getPlayerGameInstance(player);
        
        if (gameInstance != null)
        {
            
            if (blockPlaceEvent.getBlockPlaced().getLocation().getZ() > gameInstance.gameMap.maxZ || blockPlaceEvent.getBlockPlaced().getLocation().getZ() < gameInstance.gameMap.minZ)
            {
                blockPlaceEvent.setCancelled(true);
                return;
            }
            
            if (blockPlaceEvent.getBlockPlaced().getLocation().getY() > gameInstance.gameMap.maxY || blockPlaceEvent.getBlockPlaced().getLocation().getY() < gameInstance.gameMap.voidLevel)
            {
                blockPlaceEvent.setCancelled(true);
                return;
            }
            
            if (blockPlaceEvent.getBlockPlaced().getLocation().getZ() < gameInstance.gameMap.attackerBaseProtMaxZ)
            {
                GameTeam gameTeam = gameInstance.getPlayerTeam(player);
                
                if (!gameTeam.teamStage.equals(TeamStage.ATTACKING))
                {
                    blockPlaceEvent.setCancelled(true);
                    return;
                }
            }
        }
        
        PlayerMode playerMode = PlayerMode.getPlayerMode(player);
        
        if (playerMode != null)
        {
            if (!playerMode.canPlaceBlocks)
            {
                blockPlaceEvent.setCancelled(true);
                return;
            }
        }
        
        if (gameInstance != null)
        {
            Location placedLocation = blockPlaceEvent.getBlock().getLocation();
            
            for (GameTeam gameTeam : gameInstance.teams)
            {
                MapBase mapBase = gameTeam.mapBase;
                if (mapBase.isInDefenderBoundingBox(placedLocation))
                {
                    blockPlaceEvent.setCancelled(true);
                    return;
                }
                
                for (Coordinate objectiveCoord : mapBase.objectives)
                {
                    Location objectiveLocation = objectiveCoord.toLocation(gameInstance.gameWorld);
                    if (Util.blockLocationsEqual(objectiveLocation, placedLocation))
                    {
                        blockPlaceEvent.setCancelled(true);
                        return;
                    }
                }
            }
            
            if (gameInstance.gameMap.placeableBlocks.contains(blockPlaceEvent.getBlock().getType()))
            {
                blockPlaceEvent.setCancelled(false);
            }
            else
            {
                blockPlaceEvent.setCancelled(true);
            }
            
            if (!gameInstance.gameMap.enablePlacableBlocks)
            {
                blockPlaceEvent.setCancelled(false);
            }
            
            Coordinate placedCoordinate = new Coordinate(placedLocation.getBlockX(), placedLocation.getBlockY(), placedLocation.getBlockZ());
            
            gameInstance.placedBlocks.add(placedCoordinate);
            
            if (GravelFallHandler.fallingBlocks.contains(blockPlaceEvent.getBlock().getType()))
            {
                gameInstance.fallingBlockCoordinateMap.put(placedCoordinate.toString(), player);
            }
            
            if (blockPlaceEvent.getBlock().getType().equals(XMaterial.TNT.parseMaterial()))
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