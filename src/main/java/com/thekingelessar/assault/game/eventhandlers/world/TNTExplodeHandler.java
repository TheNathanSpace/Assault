package com.thekingelessar.assault.game.eventhandlers.world;

import com.thekingelessar.assault.game.GameInstance;
import com.thekingelessar.assault.game.map.MapBase;
import com.thekingelessar.assault.game.team.TeamStage;
import com.thekingelessar.assault.util.Util;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;

import java.util.List;

public class TNTExplodeHandler implements Listener
{
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event)
    {
        List<Block> blockList = event.blockList();
        for (Block block : blockList)
        {
            World world = block.getWorld();
            
            GameInstance gameInstance = GameInstance.getWorldGameInstance(world);
            if (gameInstance == null) return;
            
            for (MapBase mapBase : gameInstance.gameMap.bases)
            {
                if (Util.isInside(block.getLocation(), mapBase.defenderBoundingBox.get(0).toLocation(world), mapBase.defenderBoundingBox.get(1).toLocation(world)))
                {
                    blockList.remove(block);
                }
    
                if (block.getLocation().getZ() < gameInstance.gameMap.attackerBaseProtMinZ)
                {
                    blockList.remove(block);
                }
    
            }
            
        }
    }
}