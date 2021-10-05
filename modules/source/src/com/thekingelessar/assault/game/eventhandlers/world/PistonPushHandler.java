package com.thekingelessar.assault.game.eventhandlers.world;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.team.GameTeam;
import com.thekingelessar.assault.util.Coordinate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

import java.util.List;

public class PistonPushHandler implements Listener
{
    @EventHandler
    public void onGravelFall(BlockPistonExtendEvent blockPistonExtendEvent)
    {
        World world = blockPistonExtendEvent.getBlock().getWorld();
        GameInstance gameInstance = GameInstance.getWorldGameInstance(world);
        if (gameInstance == null)
        {
            return;
        }
        
        List<Block> blocks = blockPistonExtendEvent.getBlocks();
        boolean exit = false;
        for (GameTeam gameTeam : gameInstance.teams)
        {
            for (Coordinate coordinate : gameTeam.mapBase.objectives)
            {
                Location objectiveLocation = coordinate.toLocation(world);
                objectiveLocation.setX(objectiveLocation.getBlockX());
                objectiveLocation.setY(objectiveLocation.getBlockY());
                objectiveLocation.setZ(objectiveLocation.getBlockZ());
                
                for (Block block : blocks)
                {
                    Location blockLocation = block.getLocation();
                    if (blockLocation.equals(objectiveLocation))
                    {
                        blockPistonExtendEvent.setCancelled(true);
                        exit = true;
                        break;
                    }
                }
                if (exit) break;
            }
            if (exit) break;
        }
    }
}