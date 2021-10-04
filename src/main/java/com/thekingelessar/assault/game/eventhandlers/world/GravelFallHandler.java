package com.thekingelessar.assault.game.eventhandlers.world;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.util.Coordinate;
import com.thekingelessar.assault.util.Util;
import com.thekingelessar.assault.util.xsupport.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class GravelFallHandler implements Listener
{
    public static List<Material> fallingBlocks = Arrays.asList(XMaterial.SAND.parseMaterial(), XMaterial.GRAVEL.parseMaterial(), XMaterial.ANVIL.parseMaterial(), XMaterial.DRAGON_EGG.parseMaterial());
    
    @EventHandler
    public void onGravelFall(EntityChangeBlockEvent entityChangeBlockEvent)
    {
        if (entityChangeBlockEvent.getEntity() instanceof FallingBlock)
        {
            FallingBlock fallingBlock = (FallingBlock) entityChangeBlockEvent.getEntity();
            Block block = entityChangeBlockEvent.getBlock();
            
            if (fallingBlock.getMaterial().equals(block.getType()) && fallingBlocks.contains(block.getType()))
            {
                Location placedLocation = fallingBlock.getLocation();
                Coordinate placedCoordinate = new Coordinate(placedLocation.getBlockX(), placedLocation.getBlockY(), placedLocation.getBlockZ());
                
                GameInstance gameInstance = GameInstance.getWorldGameInstance(fallingBlock.getWorld());
                
                if (gameInstance != null)
                {
                    Player player = gameInstance.fallingBlockCoordinateMap.get(placedCoordinate.toString());
                    if (player != null)
                    {
                        gameInstance.fallingBlockCoordinateMap.remove(placedCoordinate.toString());
                        gameInstance.fallingBlockPlayerMap.put(fallingBlock, player);
                        
                        
                        gameInstance.placedBlocks.remove(placedCoordinate);
                    }
                }
            }
            else if (fallingBlocks.contains(fallingBlock.getMaterial()) && block.getType().equals(XMaterial.AIR.parseMaterial()))
            {
                Location placedLocation = fallingBlock.getLocation();
                Coordinate placedCoordinate = new Coordinate(placedLocation.getBlockX(), placedLocation.getBlockY(), placedLocation.getBlockZ());
                
                GameInstance gameInstance = GameInstance.getWorldGameInstance(fallingBlock.getWorld());
                
                if (gameInstance != null)
                {
                    gameInstance.placedBlocks.add(placedCoordinate);
                    
                    for (GameTeam gameTeam : gameInstance.teams)
                    {
                        for (Coordinate objectiveCoord : gameTeam.mapBase.objectives)
                        {
                            Location objectiveLocation = objectiveCoord.toLocation(gameInstance.gameWorld);
                            if (Util.blockLocationsEqual(objectiveLocation, placedLocation))
                            {
                                gameInstance.placedBlocks.remove(placedCoordinate);
                                entityChangeBlockEvent.setCancelled(true);
                                
                                Player player = gameInstance.fallingBlockPlayerMap.get(fallingBlock);
                                if (player != null)
                                {
                                    player.getInventory().addItem(new ItemStack(fallingBlock.getMaterial()));
                                    gameInstance.fallingBlockPlayerMap.remove(fallingBlock);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}