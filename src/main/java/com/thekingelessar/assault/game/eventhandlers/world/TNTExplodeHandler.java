package com.thekingelessar.assault.game.eventhandlers.world;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.world.map.MapBase;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

public class TNTExplodeHandler implements Listener
{
    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event)
    {
        List<Block> blockList = event.blockList();
        for (Block block : new ArrayList<>(blockList))
        {
            World world = block.getWorld();
            
            GameInstance gameInstance = GameInstance.getWorldGameInstance(world);
            if (gameInstance == null) return;
            
            for (MapBase mapBase : gameInstance.gameMap.bases)
            {
                if (mapBase.isInDefenderBoundingBox(block.getLocation()))
                {
                    blockList.remove(block);
                }
                
                if (block.getLocation().getZ() < gameInstance.gameMap.attackerBaseProtMaxZ)
                {
                    blockList.remove(block);
                }
                
            }
            
        }
    }
}